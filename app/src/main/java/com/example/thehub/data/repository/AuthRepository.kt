package com.example.thehub.data.repository

import com.example.thehub.data.model.LoginRequest
import com.example.thehub.data.model.LoginResponse
import com.example.thehub.data.model.RegisterRequest // <-- Importar
import com.example.thehub.data.model.RegisterResponse // <-- Importar
import com.example.thehub.data.remote.XanoAuthApi

class AuthRepository(private val api: XanoAuthApi) {

    suspend fun login(body: LoginRequest): LoginResponse? {
        return try {
            api.login(body)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserProfile(token: String): LoginResponse? {
        return try {
            api.getUserProfile("Bearer $token")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- NUEVO: FUNCIÓN SIGNUP ---
    suspend fun signup(body: RegisterRequest): RegisterResponse? {
        return try {
            api.signup(body)
        } catch (e: Exception) {
            e.printStackTrace()
            // Aquí captura errores (ej: email ya existe) y devuelve null
            null
        }
    }
}