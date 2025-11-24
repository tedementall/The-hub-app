package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class EditUserRequest(
    @SerializedName("name") val name: String,
    @SerializedName("region") val region: String,
    @SerializedName("comuna") val comuna: String,
    @SerializedName("address_detail") val address: String
)