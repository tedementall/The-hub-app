package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    @SerializedName("created_at") val createdAt: Long,            // timestamp numérico
    val name: String,
    val description: String,
    val price: Double,
    @SerializedName("stock_quantity") val stockQuantity: Int,
    @SerializedName("image_url") val image: ProductImage?          // objeto, NO String
) {
    val imageUrl: String?
        get() = image?.url ?: image?.path // usa lo mejor disponible
}
