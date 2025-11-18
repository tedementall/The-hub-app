package com.example.thehub.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thehub.data.model.Product
import com.example.thehub.data.repository.ProductRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Variables para guardar el estado actual
    private var currentQuery: String = ""
    private var currentCategory: String? = null

    // Función principal de búsqueda
    fun searchProducts(query: String = currentQuery, category: String? = currentCategory) {
        // Actualizamos las variables de estado
        currentQuery = query
        currentCategory = category

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Enviamos ambos parámetros a la API
                val result = repository.getProducts(
                    q = if (query.isBlank()) null else query,
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
}