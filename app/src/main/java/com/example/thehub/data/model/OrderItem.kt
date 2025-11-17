package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

/**
 * Representa un solo ítem al momento de CREAR una orden.
 * (Ajusta los campos si tu API de Xano espera otros nombres)
 */
data class OrderItem(
    @SerializedName("product_id") // El ID del producto
    val productId: Int,
    val quantity: Int, // La cantidad comprada
    val price: Double // El precio al momento de la compra
)