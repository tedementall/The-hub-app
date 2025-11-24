package com.example.thehub.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int?,
    val name: String,
    val description: String,
    val price: Double,
    @SerializedName("image_url") val imageUrl: List<ProductImage>?,
    @SerializedName("stock_quantity") val stockQuantity: Int,
    val category: String?
) : Parcelable