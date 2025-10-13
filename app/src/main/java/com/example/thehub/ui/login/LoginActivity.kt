package com.example.thehub.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.thehub.databinding.ActivityLoginBinding
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.home.HomeActivity
import kotlinx.coroutines.flow.collectLatest

class LoginActivity : ComponentActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val vm: LoginViewModel by viewModels {
        ServiceLocator.init(applicationContext)
        val repo = ServiceLocator.authRepository
        val tokenStore = ServiceLocator.tokenStore
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(repo) { token -> tokenStore.save(token) } as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val pass  = binding.etPassword.text?.toString()?.trim().orEmpty()
            vm.login(email, pass)
        }

        lifecycleScope.launchWhenStarted {
            vm.state.collectLatest { st ->
                when (st) {
                    is LoginUiState.Loading -> binding.btnLogin.isEnabled = false
                    is LoginUiState.Success -> {
                        binding.btnLogin.isEnabled = true
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                    is LoginUiState.Error -> {
                        binding.btnLogin.isEnabled = true
                        Toast.makeText(this@LoginActivity, st.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}
