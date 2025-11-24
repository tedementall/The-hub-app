package com.example.thehub.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.thehub.R
import com.example.thehub.data.model.LoginRequest
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.home.HomeActivity
import com.example.thehub.ui.signup.RegisterActivity
import com.example.thehub.utils.TokenStore
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val authRepository = ServiceLocator.authRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val tvRegisterLink: TextView = findViewById(R.id.tvRegisterLink)

        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val pass = etPassword.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = authRepository.login(LoginRequest(email, pass))

                    if (response == null) {
                        Toast.makeText(this@LoginActivity, "Contraseña incorrecta o usuario no encontrado.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }


                    val token = response.authToken

                    if (!token.isNullOrEmpty()) {

                        TokenStore.save(this@LoginActivity, token)


                        TokenStore.saveName(this@LoginActivity, response.nombre)

                        Toast.makeText(this@LoginActivity, "Bienvenido ${response.nombre}", Toast.LENGTH_SHORT).show()
                        goToHomeAndFinish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Error: El servidor no devolvió un token.", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error de conexión: ${e.message ?: "desconocido"}",
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