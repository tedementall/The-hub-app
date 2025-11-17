package com.example.thehub.data.repository

import com.example.thehub.data.model.CartProduct
import com.example.thehub.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestiona el estado del carrito de compras en memoria.
 * Utiliza un StateFlow para notificar a los observadores (como el CartFragment)
 * sobre cualquier cambio en la lista de items.
 */
class CartRepository {

    // _cartItems es la lista privada mutable
    // cartItems es la lista pública e inmutable que los fragmentos observarán
    private val _cartItems = MutableStateFlow<List<CartProduct>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    /**
     * Agrega un producto al carrito.
     * Si el producto ya existe, simplemente actualiza su cantidad.
     * Si 'quantity' es 0, lo elimina.
     */
    fun addProductToCart(product: Product, quantity: Int) {
        val currentList = _cartItems.value.toMutableList()
        val existingItem = currentList.find { it.product.id == product.id }

        if (existingItem != null) {
            // Producto ya existe, actualiza la cantidad
            existingItem.quantity += quantity
            if (existingItem.quantity <= 0) {
                // Si la cantidad llega a 0 o menos, elimina el item
                currentList.remove(existingItem)
            }
        } else if (quantity > 0) {
            // Producto nuevo, agrégalo a la lista
            currentList.add(CartProduct(product = product, quantity = quantity))
        }

        // Emite la nueva lista actualizada al StateFlow
        _cartItems.value = currentList
    }

    /**
     * Actualiza la cantidad de un producto específico en el carrito.
     */
    fun updateItemQuantity(productId: Int, newQuantity: Int) {
        val currentList = _cartItems.value.toMutableList()
        val item = currentList.find { it.product.id == productId }

        if (item != null) {
            if (newQuantity <= 0) {
                // Eliminar si la cantidad es 0
                currentList.remove(item)
            } else {
                // Actualizar cantidad
                item.quantity = newQuantity
            }
            _cartItems.value = currentList
        }
    }

    /**
     * Elimina un producto completamente del carrito.
     */
    fun removeItemFromCart(productId: Int) {
        val currentList = _cartItems.value.toMutableList()
        currentList.removeAll { it.product.id == productId }
        _cartItems.value = currentList
    }

    /**
     * Calcula el subtotal de todos los productos en el carrito.
     */
    fun getSubtotal(): Double {
        return _cartItems.value.sumOf { it.product.price * it.quantity }
    }
}