package com.example.thehub.data.remote

import com.example.thehub.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var tokenProvider: (() -> String?) = { null }
    fun setTokenProvider(p: () -> String?) { tokenProvider = p }

    private val authInterceptor = Interceptor { chain ->
        val token = tokenProvider()
        val req = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else chain.request()
        chain.proceed(req)
    }

    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun okHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logger)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    private fun retrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl) // 👈 debe terminar en "/"
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp())
            .build()

    // APIs
    val authApi: XanoAuthApi by lazy {
        retrofit(BuildConfig.XANO_AUTH_BASE).create(XanoAuthApi::class.java)
    }

    val mainApi: XanoMainApi by lazy {
        retrofit(BuildConfig.XANO_STORE_BASE).create(XanoMainApi::class.java)
    }
}
