package com.example.thehub.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize // 1. Importante para pasar el objeto entre pantallas
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

    // Estos coinciden con el JSON, no necesitan SerializedName si se llaman igual
    val region: String? = null,
    val comuna: String? = null,

    @SerializedName("address_detail")
    val direccion: String? = null,

    @SerializedName("user_type")
    val tipoUsuario: String? = null,

    // 2. Agregamos el campo status para la gestión de usuarios
    @SerializedName("status")
    val estado: String? = null

) : Parcelable {

    // Lógica para saber si es admin
    val esAdministrador: Boolean
        get() = tipoUsuario == "admin" || tipoUsuario == "administrator"
}