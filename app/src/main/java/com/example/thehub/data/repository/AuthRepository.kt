package com.example.thehub.data.repository

import com.example.thehub.data.model.LoginRequest
import com.example.thehub.data.remote.XanoAuthApi

class AuthRepository(private val api: XanoAuthApi) {

    suspend fun login(body: LoginRequest): String? {
        return try {
            api.login(body).authToken  // <- devuelve solo el token
        } catch (e: Exception) {
            null
        }
    }
}
