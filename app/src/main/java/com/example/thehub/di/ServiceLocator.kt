// app/src/main/java/com/example/thehub/di/ServiceLocator.kt
package com.example.thehub.di

import android.content.Context
import com.example.thehub.data.remote.RetrofitClient
import com.example.thehub.data.remote.UploadService
import com.example.thehub.data.remote.XanoAuthApi
import com.example.thehub.data.remote.XanoMainApi
import com.example.thehub.data.repository.AuthRepository
import com.example.thehub.data.repository.ProductRepository
import com.example.thehub.utils.TokenStore

object ServiceLocator {

    private lateinit var appContext: Context

    // Llamar una vez en Application.onCreate()
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // usa tu TokenStore.read(context)
    private val tokenProvider: () -> String? = { TokenStore.read(appContext) }

    // APIs
    private val authApi: XanoAuthApi by lazy { RetrofitClient.auth() }
    private val storeApi: XanoMainApi by lazy { RetrofitClient.store(tokenProvider) }
    val uploadService: UploadService by lazy { RetrofitClient.upload(tokenProvider) }

    // Repos
    val authRepository: AuthRepository by lazy { AuthRepository(authApi) }
    val productRepository: ProductRepository by lazy { ProductRepository(storeApi) }
}
