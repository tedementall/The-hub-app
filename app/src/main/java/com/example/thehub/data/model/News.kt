package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class News(
    val id: Int,
    @SerializedName("created_at") val createdAt: Long?,
    val title: String,
    val body: String,
    val cover: ProductImage?
)