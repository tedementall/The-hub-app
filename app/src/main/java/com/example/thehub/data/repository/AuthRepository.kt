package com.example.thehub.data.repository

import com.example.thehub.data.model.LoginRequest
import com.example.thehub.data.model.LoginResponse
import com.example.thehub.data.remote.XanoAuthApi

class AuthRepository(private val api: XanoAuthApi) {

    suspend fun login(body: LoginRequest): LoginResponse? {
        return try {
            api.login(body)  // Ahora devuelve el objeto completo
        } catch (e: Exception) {
            null
        }
    }

    // NUEVO: Método para obtener el perfil del usuario
    suspend fun getUserProfile(token: String): LoginResponse? {
        return try {
            api.getUserProfile("Bearer $token")
        } catch (e: Exception) {
            null
        }
    }
}