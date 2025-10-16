package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Double,

    @SerializedName("stock_quantity") val stockQuantity: Int,

    @SerializedName("image_url") val imageUrl: List<ProductImage>? = null
)
