package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    // usa camelCase en Kotlin y mapea al nombre real del JSON
    @SerializedName("stock_quantity") val stockQuantity: Int,
    // imágenes no son necesarias al crear; se agregan luego por PATCH
    @SerializedName("image_url") val imageUrl: List<ProductImage>? = null
)
