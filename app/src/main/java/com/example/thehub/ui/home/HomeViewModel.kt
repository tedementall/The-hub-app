package com.example.thehub.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thehub.data.model.Product
import com.example.thehub.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val loading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState(loading = true))
    val state: StateFlow<HomeState> = _state

    fun load() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repository.getProducts() }
                .onSuccess { list ->
                    _state.value = HomeState(loading = false, products = list)
                }
                .onFailure { e ->
                    _state.value = HomeState(loading = false, error = e.message)
                }
        }
    }
}
