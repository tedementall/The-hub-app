package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class OrderItem(
    val id: Int,
    @SerializedName("created_at") val createdAt: String,
    val quantity: Int,
    val price: Double,
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("product_id") val productId: Int
)
