package com.example.thehub.ui.home

import androidx.lifecycle.*
import com.example.thehub.data.model.Product
import com.example.thehub.data.repository.ProductRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: ProductRepository
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadProducts(q: String? = null) {
        viewModelScope.launch {
            try {
                val data: List<Product> = repo.getProducts(limit = 100, offset = 0, q = q)
                _products.value = data
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
