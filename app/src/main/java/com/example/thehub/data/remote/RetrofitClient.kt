// RetrofitClient.kt
package com.example.thehub.data.remote

import com.example.thehub.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private fun baseClient(extra: Interceptor? = null): OkHttpClient {
        val log = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(log)
            .apply { if (extra != null) addInterceptor(extra) }
            // timeouts útiles para upload de imágenes
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    fun auth(): XanoAuthApi =
        Retrofit.Builder()
            .baseUrl(ensureSlash(BuildConfig.XANO_AUTH_BASE))
            .addConverterFactory(GsonConverterFactory.create())
            .client(baseClient())
            .build()
            .create(XanoAuthApi::class.java)

    fun store(tokenProvider: () -> String? = { null }): XanoMainApi =
        Retrofit.Builder()
            .baseUrl(ensureSlash(BuildConfig.XANO_STORE_BASE))
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                baseClient(Interceptor { chain ->
                    val t = tokenProvider()
                    val req = if (!t.isNullOrBlank()) {
                        chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $t")
                            .build()
                    } else chain.request()
                    chain.proceed(req)
                })
            )
            .build()
            .create(XanoMainApi::class.java)

    fun upload(tokenProvider: () -> String? = { null }): UploadService =
        Retrofit.Builder()
            .baseUrl(ensureSlash(BuildConfig.XANO_STORE_BASE))
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                baseClient(Interceptor { chain ->
                    val t = tokenProvider()
                    val req = if (!t.isNullOrBlank()) {
                        chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $t")
                            .build()
                    } else chain.request()
                    chain.proceed(req)
                })
            )
            .build()
            .create(UploadService::class.java)

    // Evita errores si te olvidaste la barra final en BuildConfig
    private fun ensureSlash(base: String): String =
        if (base.endsWith("/")) base else "$base/"
}
