package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val id: Int,

    @SerializedName("name")
    val nombre: String,

    @SerializedName("email")
    val correo: String,

    @SerializedName("phone")
    val telefono: String? = null,

    @SerializedName("rut")
    val rut: String? = null,


    @SerializedName("region")
    val region: String? = null,

    @SerializedName("comuna")
    val comuna: String? = null,

    @SerializedName("address_detail")
    val direccionDetalle: String? = null,

    @SerializedName("user_type")
    val tipoUsuario: String? = null,

    @SerializedName("authToken")
    val authToken: String?
) {

    val esAdministrador: Boolean
        get() = tipoUsuario == "admin" || tipoUsuario == "administrator"
}