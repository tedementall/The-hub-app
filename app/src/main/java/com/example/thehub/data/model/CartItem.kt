package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class CartItem(
    val id: Int,
    @SerializedName("created_at") val createdAt: String,
    val quantity: Int,
    @SerializedName("cart_id") val cartId: Int,
    @SerializedName("product_id") val productId: Int
)
