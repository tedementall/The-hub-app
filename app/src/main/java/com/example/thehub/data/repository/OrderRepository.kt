package com.example.thehub.data.repository

import com.example.thehub.data.model.CreateOrderRequest
import com.example.thehub.data.model.Order
import com.example.thehub.data.remote.XanoMainApi

class OrderRepository(private val api: XanoMainApi) {

    suspend fun createOrder(request: CreateOrderRequest): Result<Order> {
        return try {
            val response = api.createOrder(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserOrders(userId: Int): Result<List<Order>> {
        return try {
            val response = api.getOrders(userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}