package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int?,
    @SerializedName("created_at") val createdAt: Long?,
    val name: String,
    val description: String,
    val price: Double,
    @SerializedName("image_url") val imageUrl: List<ProductImage>?,
    @SerializedName("stock_quantity") val stockQuantity: Int
)
