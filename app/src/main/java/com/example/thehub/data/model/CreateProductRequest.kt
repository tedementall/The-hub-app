package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class CreateProductRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("stock_quantity") val stockQuantity: Int,
    @SerializedName("category") val category: String,
    @SerializedName("image_url") val imageUrl: List<ProductImage>? = null
)