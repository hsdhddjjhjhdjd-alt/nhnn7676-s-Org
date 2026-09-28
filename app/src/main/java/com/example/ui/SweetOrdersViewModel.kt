package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.SweetItem
import com.example.data.repository.FirebaseSyncManager
import com.example.data.repository.OrderRepository
import com.example.util.AlertSoundHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class OrderStats(
    val totalCount: Int = 0,
    val newCount: Int = 0,
    val preparingCount: Int = 0,
    val readyCount: Int = 0,
    val onDeliveryCount: Int = 0,
    val completedCount: Int = 0,
    val totalRevenue: Double = 0.0
)

class SweetOrdersViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = OrderRepository(database.orderDao())
    private val soundHelper = AlertSoundHelper(application)
    private val syncManager = FirebaseSyncManager(application, repository, soundHelper)

    val isFirebaseConnected: StateFlow<Boolean> = syncManager.isFirebaseConnected
    val connectionMessage: StateFlow<String> = syncManager.connectionMessage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<OrderStatus?>(null) // null = all
    val selectedStatus: StateFlow<OrderStatus?> = _selectedStatus.asStateFlow()

    private val _isAudioAlertEnabled = MutableStateFlow(true)
    val isAudioAlertEnabled: StateFlow<Boolean> = _isAudioAlertEnabled.asStateFlow()

    private val _isAutoSimulationEnabled = MutableStateFlow(false)
    val isAutoSimulationEnabled: StateFlow<Boolean> = _isAutoSimulationEnabled.asStateFlow()

    private val _latestAlertOrder = MutableStateFlow<OrderEntity?>(null)
    val latestAlertOrder: StateFlow<OrderEntity?> = _latestAlertOrder.asStateFlow()

    private val _selectedOrderForInvoice = MutableStateFlow<OrderEntity?>(null)
    val selectedOrderForInvoice: StateFlow<OrderEntity?> = _selectedOrderForInvoice.asStateFlow()

    private val _isNewOrderDialogOpen = MutableStateFlow(false)
    val isNewOrderDialogOpen: StateFlow<Boolean> = _isNewOrderDialogOpen.asStateFlow()

    private var autoSimJob: Job? = null

    val rawOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredOrders: StateFlow<List<OrderEntity>> = combine(
        rawOrders,
        _searchQuery,
        _selectedStatus
    ) { orders, query, status ->
        orders.filter { order ->
            val matchesStatus = status == null || order.status == status
            val matchesQuery = query.isBlank() ||
                    order.customerName.contains(query, ignoreCase = true) ||
                    order.customerPhone.contains(query, ignoreCase = true) ||
                    order.orderNumber.contains(query, ignoreCase = true) ||
                    order.deliveryAddress.contains(query, ignoreCase = true) ||
                    order.deliveryArea.contains(query, ignoreCase = true)
            matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<OrderStats> = rawOrders.combine(_searchQuery) { orders, _ ->
        OrderStats(
            totalCount = orders.size,
            newCount = orders.count { it.status == OrderStatus.NEW },
            preparingCount = orders.count { it.status == OrderStatus.PREPARING },
            readyCount = orders.count { it.status == OrderStatus.READY },
            onDeliveryCount = orders.count { it.status == OrderStatus.ON_DELIVERY },
            completedCount = orders.count { it.status == OrderStatus.COMPLETED },
            totalRevenue = orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.totalPrice }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OrderStats())

    init {
        // Stop initializing mock data. Keep the database fully real for the shop.
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterSelected(status: OrderStatus?) {
        _selectedStatus.value = status
    }

    fun toggleAudioAlert() {
        _isAudioAlertEnabled.value = !_isAudioAlertEnabled.value
    }

    fun toggleAutoSimulation() {
        val nextState = !_isAutoSimulationEnabled.value
        _isAutoSimulationEnabled.value = nextState
        if (nextState) {
            startAutoSimulation()
        } else {
            autoSimJob?.cancel()
            autoSimJob = null
        }
    }

    private fun startAutoSimulation() {
        autoSimJob?.cancel()
        autoSimJob = viewModelScope.launch {
            while (isActive) {
                delay(18000) // receive a realistic new customer order every 18 seconds
                simulateIncomingOrder()
            }
        }
    }

    fun simulateIncomingOrder() {
        viewModelScope.launch {
            val newOrder = repository.generateRandomIncomingOrder()
            _latestAlertOrder.value = newOrder
            soundHelper.playNewOrderAlert(enableSound = _isAudioAlertEnabled.value, enableVibration = true)
            syncManager.publishOrderToCloud(newOrder)
        }
    }

    fun advanceOrderStatus(order: OrderEntity) {
        val nextStatus = when (order.status) {
            OrderStatus.NEW -> OrderStatus.PREPARING
            OrderStatus.PREPARING -> OrderStatus.READY
            OrderStatus.READY -> OrderStatus.ON_DELIVERY
            OrderStatus.ON_DELIVERY -> OrderStatus.COMPLETED
            OrderStatus.COMPLETED -> OrderStatus.COMPLETED
            OrderStatus.CANCELLED -> OrderStatus.CANCELLED
        }
        updateOrderStatus(order.id, nextStatus)
    }

    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus) {
        viewModelScope.launch {
            repository.updateStatus(orderId, newStatus)
        }
    }

    fun deleteOrder(orderId: Long) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
        }
    }

    fun dismissAlertBanner() {
        _latestAlertOrder.value = null
    }

    fun openInvoice(order: OrderEntity) {
        _selectedOrderForInvoice.value = order
    }

    fun closeInvoice() {
        _selectedOrderForInvoice.value = null
    }

    fun openNewOrderDialog() {
        _isNewOrderDialogOpen.value = true
    }

    fun closeNewOrderDialog() {
        _isNewOrderDialogOpen.value = false
    }

    fun createCustomOrder(
        customerName: String,
        customerPhone: String,
        address: String,
        area: String,
        deliveryTime: String,
        paymentMethod: String,
        notes: String,
        items: List<SweetItem>
    ) {
        viewModelScope.launch {
            val orderNum = "#HL-" + (100..999).random()
            val total = items.sumOf { it.quantity * it.unitPrice }
            val order = OrderEntity(
                orderNumber = orderNum,
                customerName = customerName.ifBlank { "عميل المحل" },
                customerPhone = customerPhone.ifBlank { "0500000000" },
                deliveryAddress = address.ifBlank { "الرياض - توصيل خاص" },
                deliveryArea = area.ifBlank { "الرياض" },
                deliveryTimeFormatted = deliveryTime.ifBlank { "خلال 45 دقيقة" },
                deliveryTimestamp = System.currentTimeMillis() + (45 * 60 * 1000),
                status = OrderStatus.NEW,
                paymentMethod = paymentMethod,
                isPaid = paymentMethod.contains("مدفوع"),
                customerNotes = notes,
                itemsJson = OrderEntity.itemsToJson(items),
                totalPrice = total,
                createdAt = System.currentTimeMillis()
            )
            val id = repository.insertOrder(order)
            val savedOrder = order.copy(id = id)
            _latestAlertOrder.value = savedOrder
            soundHelper.playNewOrderAlert(enableSound = _isAudioAlertEnabled.value, enableVibration = true)
            syncManager.publishOrderToCloud(savedOrder)
            closeNewOrderDialog()
        }
    }

    fun resetSampleData() {
        viewModelScope.launch {
            repository.clearAll()
            repository.initializeSampleDataIfEmpty()
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoSimJob?.cancel()
        syncManager.cleanup()
        soundHelper.release()
    }
}
