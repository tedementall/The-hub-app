package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,
    @SerializedName("created_at") val createdAt: Any?,
    @SerializedName("order_number") val orderNumber: Int,
    @SerializedName("total_amount") val totalAmount: Double,
    val status: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("items") val items: List<OrderItem>? = null
)