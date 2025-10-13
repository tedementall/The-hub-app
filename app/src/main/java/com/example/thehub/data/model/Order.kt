package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("order_date") val orderDate: String,
    @SerializedName("total_amount") val totalAmount: Double,
    val status: String,
    @SerializedName("user_id") val userId: Int
)
