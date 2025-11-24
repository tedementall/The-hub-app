package com.example.thehub.data.repository

import com.example.thehub.data.model.EditUserRequest // <-- Importante para editar
import com.example.thehub.data.model.LoginRequest
import com.example.thehub.data.model.LoginResponse
import com.example.thehub.data.model.RegisterRequest
import com.example.thehub.data.model.RegisterResponse
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


    suspend fun signup(body: RegisterRequest): RegisterResponse? {
        return try {
            api.signup(body)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun updateProfile(token: String, request: EditUserRequest): LoginResponse? {
        return try {

            api.updateProfile("Bearer $token", request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}