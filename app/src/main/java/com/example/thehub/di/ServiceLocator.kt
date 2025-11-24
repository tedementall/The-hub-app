package com.example.thehub.di

import android.content.Context
import com.example.thehub.data.remote.RetrofitClient
import com.example.thehub.data.remote.UploadService
import com.example.thehub.data.remote.XanoAuthApi
import com.example.thehub.data.remote.XanoMainApi
import com.example.thehub.data.repository.AuthRepository
import com.example.thehub.data.repository.CartRepository
import com.example.thehub.data.repository.OrderRepository
import com.example.thehub.data.repository.ProductRepository
import com.example.thehub.data.repository.UserRepository
import com.example.thehub.data.repository.BlogRepository
import com.example.thehub.utils.TokenStore

object ServiceLocator {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val tokenProvider: () -> String? = { TokenStore.read(appContext) }

    private val authApi: XanoAuthApi by lazy { RetrofitClient.auth() }


    private val storeApi: XanoMainApi by lazy { RetrofitClient.store(tokenProvider) }

    val uploadService: UploadService by lazy { RetrofitClient.upload(tokenProvider) }

    val authRepository: AuthRepository by lazy { AuthRepository(authApi) }

    val productRepository: ProductRepository by lazy { ProductRepository(storeApi) }

    val cartRepository: CartRepository = CartRepository

    val orderRepository: OrderRepository by lazy { OrderRepository(storeApi) }


    val userRepository: UserRepository by lazy { UserRepository(storeApi) }

    val blogRepository: BlogRepository by lazy { BlogRepository(storeApi) }

}