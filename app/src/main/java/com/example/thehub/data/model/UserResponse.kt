package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("items") val items: List<User>,
    @SerializedName("total") val total: Int? = 0
)