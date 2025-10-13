package com.example.thehub.repository

import com.example.thehub.data.model.LoginRequest
import com.example.thehub.data.model.LoginResponse
import com.example.thehub.data.remote.XanoAuthApi
import com.example.thehub.utils.TokenStore

class AuthRepository(
    private val api: XanoAuthApi,
    private val tokenStore: TokenStore
) {
    suspend fun login(body: LoginRequest): LoginResponse {
        val res = api.login(body)
        tokenStore.save(res.authToken)
        return res
    }

    suspend fun signup(body: LoginRequest): LoginResponse {
        val res = api.signup(body)
        tokenStore.save(res.authToken)
        return res
    }

    fun logout() = tokenStore.clear()
}
