package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("region") val region: String,
    @SerializedName("comuna") val comuna: String,

    @SerializedName("address_detail") val address: String
)