package com.example.thehub.data.repository

import com.example.thehub.data.remote.XanoMainApi

class ProductRepository(private val api: XanoMainApi) {
    suspend fun getProducts() = api.getProducts()
}
