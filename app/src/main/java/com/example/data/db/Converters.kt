package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.OrderStatus

class Converters {
    @TypeConverter
    fun fromOrderStatus(status: OrderStatus?): String {
        return status?.name ?: OrderStatus.NEW.name
    }

    @TypeConverter
    fun toOrderStatus(value: String?): OrderStatus {
        return try {
            if (value != null) OrderStatus.valueOf(value) else OrderStatus.NEW
        } catch (e: Exception) {
            OrderStatus.NEW
        }
    }
}
