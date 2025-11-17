package com.example.thehub.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de datos local para representar un producto dentro del carrito
 * en la interfaz de usuario.
 */
@Parcelize
data class CartProduct(
    val product: Product,
    var quantity: Int
) : Parcelable