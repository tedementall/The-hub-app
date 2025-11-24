package com.example.thehub.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

object TokenStore {

    private const val PREFS = "thehub_prefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_LOGGED = "is_logged_in"
    private const val KEY_USER_NAME = "user_name" // <--- Nueva clave para el nombre

    // Guardar Token y estado de sesión
    fun save(context: Context, token: String) {
        context.getSharedPreferences(PREFS, MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .putBoolean(KEY_LOGGED, true)
            .apply()
    }

    // --- NUEVO: Guardar Nombre ---
    fun saveName(context: Context, name: String) {
        context.getSharedPreferences(PREFS, MODE_PRIVATE)
            .edit()
            .putString(KEY_USER_NAME, name)
            .apply()
    }

    // Leer Token
    fun read(context: Context): String? {
        return context.getSharedPreferences(PREFS, MODE_PRIVATE)
            .getString(KEY_TOKEN, null)
    }

    // --- NUEVO: Leer Nombre ---
    fun getUserName(context: Context): String? {
        return context.getSharedPreferences(PREFS, MODE_PRIVATE)
            .getString(KEY_USER_NAME, null)
    }

    // Verificar si está logueado
    fun isLoggedIn(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, MODE_PRIVATE)
            .getBoolean(KEY_LOGGED, false) && read(context) != null
    }

    // Borrar todo (Cerrar sesión)
    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, MODE_PRIVATE)
            .edit()
            .remove(KEY_TOKEN)
            .remove(KEY_LOGGED)
            .remove(KEY_USER_NAME) // <--- También borramos el nombre
            .apply()
    }
}