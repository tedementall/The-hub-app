package com.example.thehub.data.remote

import com.example.thehub.data.model.CreateOrderRequest
import com.example.thehub.data.model.CreateProductRequest
import com.example.thehub.data.model.Order
import com.example.thehub.data.model.PatchImagesRequest
import com.example.thehub.data.model.Product
import com.example.thehub.data.model.ProductImage
import okhttp3.MultipartBody
import com.example.thehub.data.model.User
import com.example.thehub.data.model.UserResponse
import com.example.thehub.data.model.News
import com.example.thehub.data.model.CreateNewsRequest
import retrofit2.Response
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
    suspend fun createProduct(@Body body: CreateProductRequest): Product

    @Multipart
    @POST("upload/image")
    suspend fun uploadImages(@Part parts: List<MultipartBody.Part>): List<ProductImage>

    @PATCH("product/{id}")
    suspend fun patchProductImages(@Path("id") id: Int, @Body body: PatchImagesRequest): Product

    @PATCH("product/{id}")
    suspend fun editProduct(@Path("id") id: Int, @Body body: CreateProductRequest): Product

    @DELETE("product/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>



    @GET("order")
    suspend fun getOrders(@Query("user_id") userId: Int): List<Order>


    @GET("order")
    suspend fun getAllOrders(): List<Order>

    @POST("order")
    suspend fun createOrder(@Body body: CreateOrderRequest): Order

    @DELETE("order/{order_id}")
    suspend fun deleteOrder(
        @Path("order_id") orderId: Int
    ): Response<Unit>

    @PATCH("order/{order_id}")
    suspend fun updateOrderStatus(
        @Path("order_id") orderId: Int,
        @Body statusUpdate: Map<String, String>
    ): Order



    @GET("https://x8ki-letl-twmt.n7.xano.io/api:MJq6ok-f/GET_USERS")
    suspend fun getAllUsers(): UserResponse

    @DELETE("https://x8ki-letl-twmt.n7.xano.io/api:MJq6ok-f/delete_user")
    suspend fun deleteUser(@Query("user_id") userId: Int): Response<Unit>

    @POST("https://x8ki-letl-twmt.n7.xano.io/api:MJq6ok-f/UPDATE_USERS/")
    suspend fun adminUpdateUser(@Query("user_id") userId: Int, @Body updates: Map<String, String>): User

    @GET("news")
    suspend fun getNews(): List<News>

    @POST("add_news")
    suspend fun createNews(@Body body: CreateNewsRequest): Any
}