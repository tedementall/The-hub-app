package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("authToken") val authToken: String,
    @SerializedName("name") val name: String
)