package com.example.thehub.data.remote

import com.example.thehub.data.model.CreateOrderRequest
import com.example.thehub.data.model.CreateProductRequest
import com.example.thehub.data.model.Order
import com.example.thehub.data.model.PatchImagesRequest
import com.example.thehub.data.model.Product
import com.example.thehub.data.model.ProductImage
import okhttp3.MultipartBody
import retrofit2.http.*

interface XanoMainApi {


    @GET("product")
    suspend fun getProducts(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("q") q: String? = null,
        @Query("category") category: String? = null
    ): List<Product>


    @POST("product")
    suspend fun createProduct(
        @Body body: CreateProductRequest
    ): Product


    @Multipart
    @POST("upload/image")
    suspend fun uploadImages(
        @Part parts: List<MultipartBody.Part>
    ): List<ProductImage>


    @PATCH("product/{id}")
    suspend fun patchProductImages(
        @Path("id") id: Int,
        @Body body: PatchImagesRequest
    ): Product


    @POST("order")
    suspend fun createOrder(
        @Body body: CreateOrderRequest
    ): Order
}