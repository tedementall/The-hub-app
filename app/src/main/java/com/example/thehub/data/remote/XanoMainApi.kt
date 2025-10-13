package com.example.thehub.data.remote

import com.example.thehub.data.model.Product
import retrofit2.http.GET
import retrofit2.http.Path

interface XanoMainApi {
    @GET("product")
    suspend fun getProducts(): List<Product>

    @GET("product/{id}")
    suspend fun getProduct(@Path("id") id: Int): Product
}
