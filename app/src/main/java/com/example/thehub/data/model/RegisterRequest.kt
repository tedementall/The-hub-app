package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName // <-- ESTO FALTABA

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("region") val region: String,
    @SerializedName("comuna") val comuna: String,
    // En Kotlin lo llamamos 'address', pero le decimos a GSON que en el JSON de Xano busque 'address_detail'
    @SerializedName("address_detail") val address: String
)