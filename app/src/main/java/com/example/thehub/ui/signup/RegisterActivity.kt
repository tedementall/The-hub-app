package com.example.thehub.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast // <-- FALTABA
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope // <-- FALTABA
import com.example.thehub.R
import com.example.thehub.data.model.RegisterRequest
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.home.HomeActivity
import com.example.thehub.utils.ChileLocationHelper
import com.example.thehub.utils.TokenStore
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private val authRepository = ServiceLocator.authRepository
    private var selectedRegion: String? = null
    private var selectedComuna: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val etName: EditText = findViewById(R.id.etName)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val etAddressDetail: EditText = findViewById(R.id.etAddressDetail)
        val btnRegister: Button = findViewById(R.id.btnRegister)
        val tvLoginLink: TextView = findViewById(R.id.tvLoginLink)

        val tilComuna: TextInputLayout = findViewById(R.id.tilComuna)
        val autoCompleteRegion: AutoCompleteTextView = findViewById(R.id.autoCompleteRegion)
        val autoCompleteComuna: AutoCompleteTextView = findViewById(R.id.autoCompleteComuna)

        // Configuración de Regiones y Comunas
        val regions = ChileLocationHelper.getRegions()
        val regionAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, regions)
        autoCompleteRegion.setAdapter(regionAdapter)

        autoCompleteRegion.setOnItemClickListener { _, _, position, _ ->
            val region = regionAdapter.getItem(position).toString()
            selectedRegion = region
            selectedComuna = null
            autoCompleteComuna.text = null
            tilComuna.isEnabled = true

            val comunas = ChileLocationHelper.getComunas(region)
            val comunaAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, comunas)
            autoCompleteComuna.setAdapter(comunaAdapter)
        }

        autoCompleteComuna.setOnItemClickListener { adapterView, _, position, _ ->
            selectedComuna = adapterView.getItemAtPosition(position).toString()
        }

        btnBack.setOnClickListener { finish() }
        tvLoginLink.setOnClickListener { finish() }

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val address = etAddressDetail.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedRegion == null || selectedComuna == null) {
                Toast.makeText(this, "Selecciona región y comuna", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(name, email, password, selectedRegion!!, selectedComuna!!, address)
        }
    }

    private fun registerUser(
        name: String,
        email: String,
        pass: String,
        region: String,
        comuna: String,
        address: String
    ) {
        lifecycleScope.launch {
            try {
                val request = RegisterRequest(
                    name = name,
                    email = email,
                    password = pass,
                    region = region,
                    comuna = comuna,
                    address = address // Ahora coincide con el nombre en RegisterRequest.kt
                )

                val response = authRepository.signup(request)

                if (response != null) {
                    TokenStore.save(this@RegisterActivity, response.authToken)
                    Toast.makeText(this@RegisterActivity, "¡Bienvenido, ${response.name}!", Toast.LENGTH_LONG).show()

                    val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Error al registrar. Verifica el correo.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}