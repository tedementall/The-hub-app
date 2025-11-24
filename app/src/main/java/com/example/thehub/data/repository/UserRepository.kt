package com.example.thehub.data.repository

import com.example.thehub.data.model.User
import com.example.thehub.data.remote.XanoMainApi
import retrofit2.Response

class UserRepository(private val api: XanoMainApi) {

    suspend fun getAllUsers(): List<User> {
        val response = api.getAllUsers()
        return response.items
    }

    suspend fun deleteUser(id: Int): Response<Unit> {
        return api.deleteUser(id)
    }

    suspend fun adminUpdateUser(id: Int, updates: Map<String, String>): User {
        return api.adminUpdateUser(id, updates)
    }
}