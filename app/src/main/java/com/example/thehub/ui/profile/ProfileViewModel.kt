package com.example.thehub.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thehub.data.model.Address
import com.example.thehub.di.ServiceLocator
import com.example.thehub.utils.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserProfile(
    val nombre: String,
    val correo: String,
    val telefono: String?,
    val rut: String?,
    val direccion: Address?,
    val esAdministrador: Boolean
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    // Usar ServiceLocator igual que en LoginActivity
    private val authRepository = ServiceLocator.authRepository

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val token = TokenStore.read(getApplication())
                if (token.isNullOrEmpty()) {
                    _error.value = "No se encontró sesión activa"
                    _isLoading.value = false
                    return@launch
                }

                val response = authRepository.getUserProfile(token)

                if (response != null) {
                    _userProfile.value = UserProfile(
                        nombre = response.nombre,
                        correo = response.correo,
                        telefono = response.telefono,
                        rut = response.rut,
                        direccion = response.direccion,
                        esAdministrador = response.esAdministrador
                    )
                } else {
                    _error.value = "Error al cargar el perfil"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        TokenStore.clear(getApplication())
        _userProfile.value = null
    }
}