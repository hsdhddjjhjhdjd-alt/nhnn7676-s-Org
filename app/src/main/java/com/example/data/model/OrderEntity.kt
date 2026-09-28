package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

@Entity(
    tableName = "orders",
    indices = [
        Index(value = ["orderNumber"]),
        Index(value = ["createdAt"])
    ]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val deliveryArea: String,
    val deliveryTimeFormatted: String,
    val deliveryTimestamp: Long,
    val status: OrderStatus = OrderStatus.NEW,
    val paymentMethod: String = "الدفع عند الاستلام",
    val isPaid: Boolean = false,
    val customerNotes: String = "",
    val itemsJson: String = "[]",
    val totalPrice: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getItems(): List<SweetItem> {
        val list = mutableListOf<SweetItem>()
        try {
            val jsonArray = JSONArray(itemsJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    SweetItem(
                        name = obj.optString("name", "صنف حلويات"),
                        quantity = obj.optInt("quantity", 1),
                        unitPrice = obj.optDouble("unitPrice", 0.0),
                        imageDrawableName = obj.optString("imageDrawableName", "img_cake"),
                        notes = obj.optString("notes", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    companion object {
        fun itemsToJson(items: List<SweetItem>): String {
            val array = JSONArray()
            for (item in items) {
                val obj = JSONObject()
                obj.put("name", item.name)
                obj.put("quantity", item.quantity)
                obj.put("unitPrice", item.unitPrice)
                obj.put("imageDrawableName", item.imageDrawableName)
                obj.put("notes", item.notes)
                array.put(obj)
            }
            return array.toString()
        }
    }
}
