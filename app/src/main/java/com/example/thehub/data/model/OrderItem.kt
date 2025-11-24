package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName


data class OrderItem(
    @SerializedName("product_id")
    val productId: Int,
    val quantity: Int,
    val price: Double
)