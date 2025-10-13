// utils/TokenStore.kt
package com.example.thehub.utils

import android.content.Context

class TokenStore(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    fun save(token: String) = prefs.edit().putString("auth_token", token).apply()
    fun get(): String? = prefs.getString("auth_token", null)
    fun clear() = prefs.edit().remove("auth_token").apply()
}
