package com.example.thehub.data.model

import android.os.Parcelable // <-- AÑADIR IMPORT
import kotlinx.parcelize.Parcelize // <-- AÑADIR IMPORT

@Parcelize // <-- AÑADIR ESTO
data class ProductImage(
    val access: String?,
    val path: String?,
    val name: String?,
    val type: String?,
    val size: Int?,
    val mime: String?
) : Parcelable // <-- AÑADIR ESTO