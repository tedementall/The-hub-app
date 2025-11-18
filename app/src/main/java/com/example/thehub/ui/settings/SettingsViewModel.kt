package com.example.thehub.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thehub.di.ServiceLocator
import com.example.thehub.utils.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = ServiceLocator.authRepository

    // Estado para saber si es admin (empieza en falso por seguridad)
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    fun checkUserRole() {
        viewModelScope.launch {
            val token = TokenStore.read(getApplication())

            if (!token.isNullOrEmpty()) {
                try {
                    // Llamamos a la API. Esto devuelve un objeto 'LoginResponse?'
                    val user = authRepository.getUserProfile(token)

                    if (user != null) {
                        // ¡AQUÍ ESTÁ LA CLAVE!
                        // Usamos la propiedad calculada que ya escribiste en LoginResponse.kt
                        // Ella sola revisa si user_type es "admin" o "administrator"
                        _isAdmin.value = user.esAdministrador
                    } else {
                        _isAdmin.value = false
                    }
                } catch (e: Exception) {
                    _isAdmin.value = false
                }
            } else {
                _isAdmin.value = false
            }
        }
    }
}