package com.example.thehub.di

import com.example.thehub.data.remote.RetrofitClient
import com.example.thehub.data.repository.AuthRepository
import com.example.thehub.data.repository.ProductRepository

object ServiceLocator {

    // APIs
    private val authApi  by lazy { RetrofitClient.auth() }
    private val storeApi by lazy { RetrofitClient.store() }

    // Repositories
    val authRepository: AuthRepository by lazy { AuthRepository(authApi) }
    val productRepository: ProductRepository by lazy { ProductRepository(storeApi) }
}
