package com.example.thehub.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.thehub.R
import com.example.thehub.data.model.LoginRequest
import com.example.thehub.databinding.ActivityLoginBinding
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.home.HomeActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authRepository by lazy { ServiceLocator.authRepository }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animación de logo
        val anim = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale)
        findViewById<ImageView?>(R.id.ivLogoTop)?.startAnimation(anim)
        findViewById<ImageView?>(R.id.ivLogo)?.startAnimation(anim)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val pass  = binding.etPassword.text?.toString().orEmpty()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            doLogin(email, pass)
        }
    }

    private fun doLogin(email: String, pass: String) {
        binding.btnLogin.isEnabled = false
        lifecycleScope.launch {
            try {
                val token = authRepository.login(LoginRequest(email, pass))
                if (token.isNullOrBlank()) {
                    Toast.makeText(this@LoginActivity, "Token vacío", Toast.LENGTH_SHORT).show()
                    binding.btnLogin.isEnabled = true
                    return@launch
                }
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Login error: ${e.message}", Toast.LENGTH_LONG).show()
                binding.btnLogin.isEnabled = true
            }
        }
    }
}
