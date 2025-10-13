package com.example.thehub.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thehub.data.model.LoginRequest
import com.example.thehub.di.ServiceLocator
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repo = ServiceLocator.authRepository

    fun login(email: String, password: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            runCatching {
                repo.login(LoginRequest(email, password))
            }.onSuccess { onDone(true) }
                .onFailure { onDone(false) }
        }
    }

    fun signup(email: String, password: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            runCatching {
                repo.signup(LoginRequest(email, password))
            }.onSuccess { onDone(true) }
                .onFailure { onDone(false) }
        }
    }
}
