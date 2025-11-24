package com.example.thehub.data.remote

import com.example.thehub.data.model.LoginRequest
import com.example.thehub.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface XanoApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("auth/me")
    suspend fun me(): Any
}
