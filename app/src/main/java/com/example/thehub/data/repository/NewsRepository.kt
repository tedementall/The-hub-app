package com.example.thehub.data.repository

import com.example.thehub.data.model.CreateNewsRequest
import com.example.thehub.data.model.News
import com.example.thehub.data.model.ProductImage
import com.example.thehub.data.remote.XanoMainApi
import okhttp3.MultipartBody

class NewsRepository(private val api: XanoMainApi) {


    suspend fun getNews(): List<News> {
        return api.getNews()
    }

    suspend fun uploadNewsImage(parts: List<MultipartBody.Part>): ProductImage? {
        return try {
            val response = api.uploadImages(parts)
            response.firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createNews(token: String, request: CreateNewsRequest): Boolean {
        return try {
            api.createNews(request)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}