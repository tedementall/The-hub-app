package com.example.thehub.data.repository

import com.example.thehub.data.model.News
import com.example.thehub.data.remote.XanoMainApi

class BlogRepository(private val api: XanoMainApi) {

    suspend fun getNews(): List<News> {
        return api.getNews()
    }
}