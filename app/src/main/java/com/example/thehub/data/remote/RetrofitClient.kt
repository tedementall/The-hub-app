package com.example.thehub.data.remote

import com.example.thehub.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private fun baseClient(): OkHttpClient {
        val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .addInterceptor(log)
            .build()
    }

    fun auth(): XanoAuthApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.XANO_AUTH_BASE)   // <-- SIEMPRE /api:MJq6ok-f/
            .addConverterFactory(GsonConverterFactory.create())
            .client(baseClient())
            .build()
            .create(XanoAuthApi::class.java)

    fun store(): XanoMainApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.XANO_STORE_BASE)  // <-- SIEMPRE /api:Ekf2eplz/
            .addConverterFactory(GsonConverterFactory.create())
            .client(baseClient())
            .build()
            .create(XanoMainApi::class.java)
}
