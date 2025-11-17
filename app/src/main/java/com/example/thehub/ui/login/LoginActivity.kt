package com.example.thehub.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.thehub.R
import com.example.thehub.data.model.LoginRequest
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.home.HomeActivity
import com.example.thehub.utils.TokenStore
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val authRepository = ServiceLocator.authRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (TokenStore.isLoggedIn(this)) {
            goToHomeAndFinish()
            return
        }

        setContentView(R.layout.activity_login)

        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val pass = etPassword.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // CAMBIO: Ahora recibe LoginResponse en lugar de String
                    val response = authRepository.login(LoginRequest(email, pass))

                    if (response == null) {
                        Toast.makeText(this@LoginActivity, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // CAMBIO: Guardar el token del objeto response
                    TokenStore.save(this@LoginActivity, response.authToken)

                    // Opcional: Mostrar mensaje de bienvenida
                    Toast.makeText(this@LoginActivity, "Bienvenido ${response.nombre}", Toast.LENGTH_SHORT).show()

                    goToHomeAndFinish()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login error: ${e.message ?: "desconocido"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun goToHomeAndFinish() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
        finish()
    }
}