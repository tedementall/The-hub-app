package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class Address(
    val id: Int,
    @SerializedName("created_at") val createdAt: String,
    val street: String,
    val commune: String,
    val region: String,
    @SerializedName("zip_code") val zipCode: String
)
