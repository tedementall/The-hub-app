package com.example.thehub.utils

import android.content.Context

class TokenStore(ctx: Context) {
    private val prefs = ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun save(token: String?) { prefs.edit().putString("token", token).apply() }
    fun get(): String? = prefs.getString("token", null)
    fun clear() { prefs.edit().clear().apply() }
}
