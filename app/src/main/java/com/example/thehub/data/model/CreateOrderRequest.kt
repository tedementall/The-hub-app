package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que enviamos a la API para crear una nueva orden.
 */
data class CreateOrderRequest(
    @SerializedName("address_id")
    val addressId: Int, // El ID de la dirección de envío

    @SerializedName("total_amount")
    val totalAmount: Double, // El monto total calculado

    val status: String, // Ej: "pendiente", "procesando"

    val items: List<OrderItem> // La lista de productos
)