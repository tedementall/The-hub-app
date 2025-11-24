package com.example.thehub.di

import android.content.Context
import com.example.thehub.data.remote.RetrofitClient
import com.example.thehub.data.remote.UploadService
import com.example.thehub.data.remote.XanoAuthApi
import com.example.thehub.data.remote.XanoMainApi
import com.example.thehub.data.repository.AuthRepository
import com.example.thehub.data.repository.CartRepository // <-- AÑADIR IMPORT
import com.example.thehub.data.repository.ProductRepository
import com.example.thehub.utils.TokenStore

object ServiceLocator {

    private lateinit var appContext: Context


    fun init(context: Context) {
        appContext = context.applicationContext
    }


    private val tokenProvider: () -> String? = { TokenStore.read(appContext) }

    // APIs
    private val authApi: XanoAuthApi by lazy { RetrofitClient.auth() }
    private val storeApi: XanoMainApi by lazy { RetrofitClient.store(tokenProvider) }
    val uploadService: UploadService by lazy { RetrofitClient.upload(tokenProvider) }

    // Repos
    val authRepository: AuthRepository by lazy { AuthRepository(authApi) }
    val productRepository: ProductRepository by lazy { ProductRepository(storeApi) }


    // Esto crea una instancia única del repositorio del carrito
    val cartRepository: CartRepository by lazy { CartRepository }

}