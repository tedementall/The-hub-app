package com.example.thehub.data.remote

import com.example.thehub.data.model.LoginRequest
import com.example.thehub.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface XanoAuthApi {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    // NUEVO: Endpoint para obtener el perfil del usuario autenticado
    @GET("auth/me")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): LoginResponse
}