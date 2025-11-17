package com.example.thehub.data.remote

import com.example.thehub.data.model.CreateOrderRequest
import com.example.thehub.data.model.CreateProductRequest
import com.example.thehub.data.model.Order
import com.example.thehub.data.model.PatchImagesRequest
import com.example.thehub.data.model.Product
import retrofit2.http.*

interface XanoMainApi {

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

    /**
     * ¡¡IMPORTANTE!!
     * Esta es la función que FALTA.
     * Tienes que confirmar el endpoint (ej: "order") y el modelo de respuesta
     * con tu API de Xano.
     */
    @POST("order") // <-- ¿Es este el endpoint correcto?
    suspend fun createOrder(
        @Body body: CreateOrderRequest
    ): Order // <-- ¿Devuelve un 'Order'?

}