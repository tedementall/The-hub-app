package com.example.thehub.data.repository

import com.example.thehub.data.model.CartProduct
import com.example.thehub.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object CartRepository {


    private val _cartItems = MutableStateFlow<List<CartProduct>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    fun addProductToCart(product: Product, quantity: Int) {
        val currentList = _cartItems.value.toMutableList()
        val existingItem = currentList.find { it.product.id == product.id }

        if (existingItem != null) {

            existingItem.quantity += quantity
            if (existingItem.quantity <= 0) {

                currentList.remove(existingItem)
            }
        } else if (quantity > 0) {

            currentList.add(CartProduct(product = product, quantity = quantity))
        }


        _cartItems.value = currentList
    }


    fun updateItemQuantity(productId: Int, newQuantity: Int) {
        val currentList = _cartItems.value.toMutableList()
        val item = currentList.find { it.product.id == productId }

        if (item != null) {
            if (newQuantity <= 0) {

                currentList.remove(item)
            } else {

                item.quantity = newQuantity
            }
            _cartItems.value = currentList
        }
    }


    fun removeItemFromCart(productId: Int) {
        val currentList = _cartItems.value.toMutableList()
        currentList.removeAll { it.product.id == productId }
        _cartItems.value = currentList
    }


    fun getSubtotal(): Double {
        return _cartItems.value.sumOf { it.product.price * it.quantity }
    }
}