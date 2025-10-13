package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class Cart(
    val id: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("user_id") val userId: Int
)
