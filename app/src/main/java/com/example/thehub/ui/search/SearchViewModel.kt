package com.example.thehub.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thehub.data.model.Product
import com.example.thehub.data.repository.CartRepository
import com.example.thehub.data.repository.ProductRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: ProductRepository) : ViewModel() {


    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentQuery: String = ""
    private var currentCategory: String? = null

    fun searchProducts(query: String = currentQuery, category: String? = currentCategory) {

        currentQuery = query
        currentCategory = category

        viewModelScope.launch {
            _isLoading.value = true
            try {

                val safeQuery = if (query.isBlank()) null else query

                val result = repository.getProducts(
                    q = safeQuery,
                    category = category
                )
                _products.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                _products.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(product: Product, quantity: Int) {
        viewModelScope.launch {
            try {

                CartRepository.addProductToCart(product, quantity)

                println("DEBUG: Agregado al Carrito Global -> ${product.name} x $quantity")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}