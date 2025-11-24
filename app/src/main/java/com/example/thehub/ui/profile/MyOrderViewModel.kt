package com.example.thehub.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thehub.data.model.Order
import com.example.thehub.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyOrdersViewModel : ViewModel() {

    private val orderRepository = ServiceLocator.orderRepository

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadOrders(userId: Int) {
        _isLoading.value = true
        viewModelScope.launch {

            val result = orderRepository.getUserOrders(userId)

            result.onSuccess { list ->

                _orders.value = list.sortedByDescending { it.id }
            }.onFailure {

            }
            _isLoading.value = false
        }
    }
}