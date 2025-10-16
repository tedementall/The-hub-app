package com.example.thehub.data.repository

import com.example.thehub.data.model.CreateProductRequest
import com.example.thehub.data.model.PatchImagesRequest
import com.example.thehub.data.model.Product
import com.example.thehub.data.remote.XanoMainApi

class ProductRepository(
    private val api: XanoMainApi
) {

    suspend fun getProducts(
        limit: Int? = null,
        offset: Int? = null,
        q: String? = null
    ): List<Product> = api.getProducts(limit, offset, q)

    suspend fun createProduct(body: CreateProductRequest): Product =
        api.createProduct(body)

    suspend fun patchImages(id: Int, body: PatchImagesRequest): Product =
        api.patchProductImages(id, body)
}
