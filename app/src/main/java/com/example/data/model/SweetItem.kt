package com.example.data.model

import com.example.R

data class SweetItem(
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
    val imageDrawableName: String = "img_cake",
    val notes: String = ""
) {
    val totalPrice: Double
        get() = quantity * unitPrice

    fun getDrawableRes(): Int {
        return when (imageDrawableName) {
            "img_kunafa" -> R.drawable.img_kunafa
            "img_baklava" -> R.drawable.img_baklava
            "img_cake" -> R.drawable.img_cake
            else -> R.drawable.img_cake
        }
    }
}
