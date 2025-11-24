package com.example.thehub.data.repository

import com.example.thehub.data.model.CreateProductRequest
import com.example.thehub.data.model.PatchImagesRequest
import com.example.thehub.data.model.Product
import com.example.thehub.data.model.ProductImage
import com.example.thehub.data.remote.XanoMainApi
import okhttp3.MultipartBody

class ProductRepository(private val api: XanoMainApi) {

    suspend fun getProducts(
        limit: Int? = null,
        offset: Int? = null,
        q: String? = null,
        category: String? = null
    ): List<Product> {
        return try {
            api.getProducts(limit, offset, q, category)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun createProduct(token: String, request: CreateProductRequest): Product? {
        return try {
            api.createProduct(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun uploadImages(images: List<MultipartBody.Part>): List<ProductImage>? {
        return try {
            api.uploadImages(images)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun patchProductImages(productId: Int, request: PatchImagesRequest): Product? {
        return try {
            api.patchProductImages(productId, request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}