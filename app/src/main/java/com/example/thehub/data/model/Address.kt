package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class Address(
    val id: Int? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("street")
    val calle: String = "",

    @SerializedName("commune")
    val comuna: String = "",

    @SerializedName("region")
    val region: String = "",

    @SerializedName("zip_code")
    val codigoPostal: String? = null,

    // Campos que no existen en Xano pero los necesita el ProfileFragment
    val numero: String = "",
    val depto: String = ""
)