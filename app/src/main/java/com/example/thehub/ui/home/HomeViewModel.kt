package com.example.thehub.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thehub.di.ServiceLocator
import com.example.thehub.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repo = ServiceLocator.productRepository

    private val _list = MutableStateFlow<List<Product>>(emptyList())
    val list: StateFlow<List<Product>> = _list

    fun load() {
        viewModelScope.launch {
            runCatching { repo.list() }
                .onSuccess { _list.value = it }
                .onFailure { /* handle/log */ }
        }
    }
}
