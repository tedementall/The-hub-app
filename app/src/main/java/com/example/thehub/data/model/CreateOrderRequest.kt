package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    @SerializedName("address_id")
    val addressId: Int,

    @SerializedName("total_amount")
    val totalAmount: Double,

    val status: String,


    @SerializedName("user_id")
    val userId: Int,

    val items: List<OrderItem>
)