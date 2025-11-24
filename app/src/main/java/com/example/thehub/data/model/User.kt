package com.example.thehub.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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

    @SerializedName("address_detail")
    val direccion: String? = null,

    @SerializedName("user_type")
    val tipoUsuario: String? = null,

    @SerializedName("status")
    val estado: String? = null

) : Parcelable {

    val esAdministrador: Boolean
        get() = tipoUsuario == "admin" || tipoUsuario == "administrator"
}