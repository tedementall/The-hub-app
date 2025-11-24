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


    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    fun checkUserRole() {
        viewModelScope.launch {
            val token = TokenStore.read(getApplication())

            if (!token.isNullOrEmpty()) {
                try {

                    val user = authRepository.getUserProfile(token)

                    if (user != null) {

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