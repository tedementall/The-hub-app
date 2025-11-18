package com.example.thehub.data.model

import android.os.Parcelable // <-- Asegúrate de tener el import
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize // <-- Asegúrate de tener el import

@Parcelize // <-- AÑADIR ESTO
data class Product(
    val id: Int?,
    val name: String,
    val description: String,
    val price: Double,
    @SerializedName("image_url") val imageUrl: List<ProductImage>?,
    @SerializedName("stock_quantity") val stockQuantity: Int
) : Parcelable // <-- AÑADIR ESTO