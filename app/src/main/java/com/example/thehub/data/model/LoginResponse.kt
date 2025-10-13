package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName(value = "authToken", alternate = ["token"])
    val authToken: String?
)
