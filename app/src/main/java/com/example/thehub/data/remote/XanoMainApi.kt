package com.example.thehub.data.remote

import com.example.thehub.data.model.CreateProductRequest
import com.example.thehub.data.model.PatchImagesRequest
import com.example.thehub.data.model.Product
import retrofit2.http.*

interface XanoMainApi {

    // NUEVO: listar productos
    @GET("product")
    suspend fun getProducts(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("q") q: String? = null
    ): List<Product>

    @POST("product")
    suspend fun createProduct(
        @Body body: CreateProductRequest
    ): Product

    @PATCH("product/{id}")
    suspend fun patchProductImages(
        @Path("id") id: Int,
        @Body body: PatchImagesRequest
    ): Product
}
