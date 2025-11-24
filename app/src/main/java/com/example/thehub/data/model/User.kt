package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,

    @SerializedName("name")
    val nombre: String,

    @SerializedName("email")
    val correo: String,

    @SerializedName("phone")
    val telefono: String? = null,

    @SerializedName("rut")
    val rut: String? = null,

    val region: String? = null,
    val comuna: String? = null,

    // Aquí está el TRUCO: Mapeamos "address_detail" (Xano) a "direccion" (Android)
    @SerializedName("address_detail")
    val direccion: String? = null,

    @SerializedName("user_type")
    val tipoUsuario: String? = null
) {
    // Esta propiedad calculada permite usar "user.esAdministrador" en el código
    val esAdministrador: Boolean
        get() = tipoUsuario == "admin" || tipoUsuario == "administrator"
}