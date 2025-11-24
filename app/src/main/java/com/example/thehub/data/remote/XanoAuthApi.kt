package com.example.thehub.data.remote

import com.example.thehub.data.model.LoginRequest
import com.example.thehub.data.model.LoginResponse
import com.example.thehub.data.model.RegisterRequest
import com.example.thehub.data.model.RegisterResponse
import com.example.thehub.data.model.EditUserRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface XanoAuthApi {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("auth/me")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): LoginResponse


    @POST("auth/signup")
    suspend fun signup(@Body body: RegisterRequest): RegisterResponse

    @POST("auth/update")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: EditUserRequest
    ): LoginResponse



}