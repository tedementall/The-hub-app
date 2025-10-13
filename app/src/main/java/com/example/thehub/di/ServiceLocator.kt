// app/src/main/java/com/example/thehub/di/ServiceLocator.kt
package com.example.thehub.di

import android.content.Context
import com.example.thehub.data.remote.RetrofitClient
import com.example.thehub.data.remote.XanoAuthApi
import com.example.thehub.data.remote.XanoMainApi
import com.example.thehub.repository.AuthRepository
import com.example.thehub.repository.ProductRepository
import com.example.thehub.utils.TokenStore

/**
 * Punto central para inicializar y compartir dependencias simples (sin Dagger/Hilt).
 * Debe llamarse una sola vez desde Application.onCreate() o desde MainActivity.onCreate().
 */
object ServiceLocator {

    // ---- Stores ----
    lateinit var tokenStore: TokenStore
        private set

    // ---- APIs ----
    lateinit var authApi: XanoAuthApi
        private set
    lateinit var mainApi: XanoMainApi
        private set

    // ---- Repos ----
    lateinit var authRepository: AuthRepository
        private set
    lateinit var productRepository: ProductRepository
        private set

    /**
     * Inicializa el grafo de dependencias. Es idempotente.
     */
    fun init(appContext: Context) {
        if (this::tokenStore.isInitialized) return

        // Storage local del token
        tokenStore = TokenStore(appContext)

        // El interceptor de Retrofit usará este provider para poner Authorization: Bearer <token>
        RetrofitClient.setTokenProvider { tokenStore.get() }

        // Clients Retrofit ya configurados con sus base URLs (AUTH y STORE)
        authApi = RetrofitClient.authApi
        mainApi = RetrofitClient.mainApi

        // Repositorios
        authRepository = AuthRepository(authApi, tokenStore)
        productRepository = ProductRepository(mainApi)
    }
}
