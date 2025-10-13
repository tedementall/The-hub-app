package com.example.thehub.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.thehub.R
import com.example.thehub.data.model.LoginRequest
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.home.HomeActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // Views
    private lateinit var ivLogoTop: ImageView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    // Repo
    private val authRepository = ServiceLocator.authRepository

    // Prefs
    private val prefs by lazy {
        getSharedPreferences("auth", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Si ya hay token guardado, ir directo al Home
        readToken()?.let { token ->
            if (token.isNotBlank()) {
                goToHome()
                finish()
                return
            }
        }

        // Referencias a vistas
        ivLogoTop = findViewById(R.id.ivLogoTop)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // Animación del logo
        runCatching {
            ivLogoTop.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in_scale)
            )
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val pass = etPassword.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            doLogin(email, pass)
        }
    }

    private fun doLogin(email: String, pass: String) {
        btnLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                val token = authRepository.login(LoginRequest(email, pass))
                if (token.isNullOrBlank()) {
                    Toast.makeText(this@LoginActivity, "Token vacío", Toast.LENGTH_SHORT).show()
                    btnLogin.isEnabled = true
                    return@launch
                }

                saveToken(token)
                goToHome()
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Login error: ${e.message ?: "desconocido"}",
                    Toast.LENGTH_SHORT
                ).show()
                btnLogin.isEnabled = true
            }
        }
    }

    private fun goToHome() {
        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
    }

    private fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    private fun readToken(): String? = prefs.getString("auth_token", null)
}
