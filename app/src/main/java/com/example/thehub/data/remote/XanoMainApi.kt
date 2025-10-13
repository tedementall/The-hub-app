package com.example.thehub.data.remote

import com.example.thehub.data.model.Product
import retrofit2.http.GET

interface XanoMainApi {
    // GET /product (base: XANO_STORE_BASE)
    @GET("product")
    suspend fun getProducts(): List<Product>
}
