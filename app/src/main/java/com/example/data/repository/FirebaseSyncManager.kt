package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.SweetItem
import com.example.util.AlertSoundHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirebaseSyncManager(
    private val context: Context,
    private val repository: OrderRepository,
    private val soundHelper: AlertSoundHelper
) {
    private val TAG = "FirebaseSyncManager"
    private var firestore: FirebaseFirestore? = null
    private var listenerRegistration: ListenerRegistration? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isFirebaseConnected = MutableStateFlow(false)
    val isFirebaseConnected: StateFlow<Boolean> = _isFirebaseConnected.asStateFlow()

    private val _connectionMessage = MutableStateFlow("جاري التحقق من الاتصال السحابي...")
    val connectionMessage: StateFlow<String> = _connectionMessage.asStateFlow()

    init {
        setupFirebase()
    }

    private fun setupFirebase() {
        try {
            // Check if Firebase is configured / initialized
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                val databaseId = "ai-studio-remixremixroyalp-3f893ee8-8a3e-483b-bc91-43706eb9d572"
                firestore = try {
                    FirebaseFirestore.getInstance(databaseId)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to initialize custom named Firestore database, falling back to default.", e)
                    FirebaseFirestore.getInstance()
                }
                _isFirebaseConnected.value = true
                _connectionMessage.value = "متصل بقاعدة البيانات السحابية 🟢"
                startListeningForOrders()
                Log.d(TAG, "Firebase initialized successfully and Firestore is ready with DB: $databaseId")
            } else {
                _isFirebaseConnected.value = false
                _connectionMessage.value = "غير متصل (يعمل محلياً بالتلقائي) ⚠️ يرجى إضافة google-services.json للتفعيل السحابي"
                Log.w(TAG, "Firebase is not initialized (google-services.json might be missing). Running in offline/simulation mode.")
            }
        } catch (e: Exception) {
            _isFirebaseConnected.value = false
            _connectionMessage.value = "فشل الاتصال بالسحابة 🔴 يرجى تهيئة Firebase"
            Log.e(TAG, "Error setting up Firebase: ${e.message}", e)
        }
    }

    private fun startListeningForOrders() {
        val fs = firestore ?: return
        try {
            // Listen to orders added/updated in "orders" collection (removed orderBy for robust index-free sync)
            listenerRegistration = fs.collection("orders")
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Log.e(TAG, "Firestore listen failed: ${error.message}", error)
                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        for (dc in snapshots.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                val doc = dc.document
                                scope.launch {
                                    processIncomingCloudOrder(doc.id, doc.data ?: emptyMap())
                                }
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting Firestore listener: ${e.message}", e)
        }
    }

    private suspend fun processIncomingCloudOrder(docId: String, data: Map<String, Any>) {
        val rawOrderNum = data["orderNumber"] ?: data["order_number"] ?: data["orderId"] ?: data["id"] ?: docId
        val orderNumber = when (rawOrderNum) {
            is String -> if (rawOrderNum.isNotBlank()) rawOrderNum else docId
            is Number -> "#HL-${rawOrderNum.toLong()}"
            else -> docId
        }

        val customerName = (data["customerName"] ?: data["customer_name"] ?: data["name"] ?: data["customer"]) as? String ?: "عميل سحابي"
        val customerPhone = (data["customerPhone"] ?: data["customer_phone"] ?: data["phone"] ?: data["mobile"]) as? String ?: "0500000000"
        val deliveryAddress = (data["deliveryAddress"] ?: data["delivery_address"] ?: data["address"] ?: "") as? String ?: "توصيل خاص"
        val deliveryArea = (data["deliveryArea"] ?: data["delivery_area"] ?: data["area"] ?: "") as? String ?: "الرياض"
        val deliveryTimeFormatted = (data["deliveryTimeFormatted"] ?: data["delivery_time"] ?: data["time"]) as? String ?: "اليوم"
        
        val deliveryTimestamp = when (val dt = data["deliveryTimestamp"] ?: data["delivery_timestamp"] ?: data["timestamp"]) {
            is Number -> dt.toLong()
            is String -> dt.toLongOrNull() ?: (System.currentTimeMillis() + 45 * 60 * 1000)
            else -> System.currentTimeMillis() + 45 * 60 * 1000
        }
        
        val statusString = (data["status"] ?: data["state"] ?: OrderStatus.NEW.name) as? String ?: OrderStatus.NEW.name
        val status = try { OrderStatus.valueOf(statusString.uppercase()) } catch (e: Exception) { OrderStatus.NEW }
        
        val paymentMethod = (data["paymentMethod"] ?: data["payment_method"] ?: data["payment"] ?: "الدفع عند الاستلام") as? String ?: "الدفع عند الاستلام"
        val isPaid = when (val ip = data["isPaid"] ?: data["is_paid"] ?: data["paid"]) {
            is Boolean -> ip
            is String -> ip.toBoolean()
            is Number -> ip.toInt() == 1
            else -> false
        }
        
        val customerNotes = (data["customerNotes"] ?: data["customer_notes"] ?: data["notes"] ?: data["note"] ?: "") as? String ?: ""
        
        val itemsJson = when {
            data["itemsJson"] is String -> data["itemsJson"] as String
            data["items_json"] is String -> data["items_json"] as String
            data["items"] is List<*> -> {
                val list = data["items"] as List<*>
                val sweetItems = list.mapNotNull { itemObj ->
                    if (itemObj is Map<*, *>) {
                        val name = (itemObj["name"] ?: itemObj["title"] ?: itemObj["item"] ?: "حلوى") as? String ?: "حلوى"
                        val qty = (itemObj["quantity"] ?: itemObj["qty"] ?: itemObj["count"] ?: 1) as? Number ?: 1
                        val price = (itemObj["unitPrice"] ?: itemObj["price"] ?: itemObj["cost"] ?: 0.0) as? Number ?: 0.0
                        val img = (itemObj["imageDrawableName"] ?: itemObj["image"] ?: "img_cake") as? String ?: "img_cake"
                        val notes = (itemObj["notes"] ?: itemObj["note"] ?: "") as? String ?: ""
                        SweetItem(
                            name = name,
                            quantity = qty.toInt(),
                            unitPrice = price.toDouble(),
                            imageDrawableName = img,
                            notes = notes
                        )
                    } else null
                }
                OrderEntity.itemsToJson(sweetItems)
            }
            data["products"] is List<*> -> {
                val list = data["products"] as List<*>
                val sweetItems = list.mapNotNull { itemObj ->
                    if (itemObj is Map<*, *>) {
                        val name = (itemObj["name"] ?: itemObj["title"] ?: "حلوى") as? String ?: "حلوى"
                        val qty = (itemObj["quantity"] ?: itemObj["qty"] ?: 1) as? Number ?: 1
                        val price = (itemObj["unitPrice"] ?: itemObj["price"] ?: 0.0) as? Number ?: 0.0
                        val img = (itemObj["imageDrawableName"] ?: itemObj["image"] ?: "img_cake") as? String ?: "img_cake"
                        val notes = (itemObj["notes"] ?: itemObj["note"] ?: "") as? String ?: ""
                        SweetItem(
                            name = name,
                            quantity = qty.toInt(),
                            unitPrice = price.toDouble(),
                            imageDrawableName = img,
                            notes = notes
                        )
                    } else null
                }
                OrderEntity.itemsToJson(sweetItems)
            }
            else -> "[]"
        }
        
        val totalPrice = when (val total = data["totalPrice"] ?: data["total_price"] ?: data["total"] ?: data["price"] ?: data["amount"]) {
            is Number -> total.toDouble()
            is String -> total.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
        
        val createdAt = when (val ca = data["createdAt"] ?: data["created_at"] ?: data["time_created"] ?: data["timestamp"]) {
            is Number -> ca.toLong()
            is String -> ca.toLongOrNull() ?: System.currentTimeMillis()
            else -> System.currentTimeMillis()
        }

        // Check if we already have this order in local Room DB
        val existingOrders = repository.orderDao.getAllOrdersList()
        val existing = existingOrders.find { it.orderNumber == orderNumber }
        
        if (existing == null) {
            val incomingOrder = OrderEntity(
                orderNumber = orderNumber,
                customerName = customerName,
                customerPhone = customerPhone,
                deliveryAddress = deliveryAddress,
                deliveryArea = deliveryArea,
                deliveryTimeFormatted = deliveryTimeFormatted,
                deliveryTimestamp = deliveryTimestamp,
                status = status,
                paymentMethod = paymentMethod,
                isPaid = isPaid,
                customerNotes = customerNotes,
                itemsJson = itemsJson,
                totalPrice = totalPrice,
                createdAt = createdAt
            )

            withContext(Dispatchers.Main) {
                // Save locally
                repository.insertOrder(incomingOrder)
                
                // Play notification alert sound & vibration!
                // Only alert for newer orders (e.g. created in last 10 minutes) to avoid beeping for old historical loads
                if (System.currentTimeMillis() - createdAt < 10 * 60 * 1000) {
                    soundHelper.playNewOrderAlert(enableSound = true, enableVibration = true)
                }
            }
        } else {
            // If order exists, ensure status or details are kept up to date
            if (existing.status != status || existing.totalPrice != totalPrice) {
                val updated = existing.copy(
                    status = status,
                    deliveryAddress = deliveryAddress,
                    customerNotes = customerNotes,
                    totalPrice = totalPrice
                )
                withContext(Dispatchers.Main) {
                    repository.insertOrder(updated)
                }
            }
        }
    }

    /**
     * Helper to publish a local order to Firestore (for two-way sync, or if management app simulates/updates)
     */
    fun publishOrderToCloud(order: OrderEntity) {
        val fs = firestore ?: return
        scope.launch {
            try {
                val orderMap = hashMapOf(
                    "orderNumber" to order.orderNumber,
                    "customerName" to order.customerName,
                    "customerPhone" to order.customerPhone,
                    "deliveryAddress" to order.deliveryAddress,
                    "deliveryArea" to order.deliveryArea,
                    "deliveryTimeFormatted" to order.deliveryTimeFormatted,
                    "deliveryTimestamp" to order.deliveryTimestamp,
                    "status" to order.status.name,
                    "paymentMethod" to order.paymentMethod,
                    "isPaid" to order.isPaid,
                    "customerNotes" to order.customerNotes,
                    "itemsJson" to order.itemsJson,
                    "totalPrice" to order.totalPrice,
                    "createdAt" to order.createdAt
                )
                fs.collection("orders")
                    .document(order.orderNumber.replace("#", "HL-"))
                    .set(orderMap)
                    .addOnSuccessListener {
                        Log.d(TAG, "Order ${order.orderNumber} successfully published to Firestore.")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error publishing order to Firestore", e)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error in publishOrderToCloud: ${e.message}", e)
            }
        }
    }

    /**
     * Clean up listeners when VM is cleared
     */
    fun cleanup() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}
