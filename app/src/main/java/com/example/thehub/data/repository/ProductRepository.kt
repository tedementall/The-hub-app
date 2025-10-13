package com.example.thehub.repository

import com.example.thehub.data.model.Product
import com.example.thehub.data.remote.XanoMainApi

class ProductRepository(
    private val api: XanoMainApi
) {
    suspend fun list(): List<Product> = api.getProducts()
    suspend fun get(id: Int): Product = api.getProduct(id)
}
