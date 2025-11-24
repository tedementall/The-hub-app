package com.example.thehub.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.thehub.data.model.RegisterRequest
import com.example.thehub.databinding.ActivityRegisterBinding
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.home.HomeActivity
import com.example.thehub.utils.ChileLocationHelper
import com.example.thehub.utils.TokenStore
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authRepository = ServiceLocator.authRepository
    private var selectedRegion: String? = null
    private var selectedComuna: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDropdowns()
        setupListeners()
    }

    private fun setupDropdowns() {
        val regions = ChileLocationHelper.getRegions()
        val regionAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, regions)
        binding.autoCompleteRegion.setAdapter(regionAdapter)

        binding.autoCompleteRegion.setOnItemClickListener { _, _, position, _ ->
            val region = regionAdapter.getItem(position).toString()
            selectedRegion = region
            selectedComuna = null
            binding.autoCompleteComuna.text = null
            binding.tilComuna.isEnabled = true

            val comunas = ChileLocationHelper.getComunas(region)
            val comunaAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, comunas)
            binding.autoCompleteComuna.setAdapter(comunaAdapter)
        }

        binding.autoCompleteComuna.setOnItemClickListener { adapterView, _, position, _ ->
            selectedComuna = adapterView.getItemAtPosition(position).toString()
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.tvLoginLink.setOnClickListener { finish() }

        binding.btnRegister.setOnClickListener {
            validateAndRegister()
        }
    }

    private fun validateAndRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val address = binding.etAddressDetail.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedRegion == null || selectedComuna == null) {
            Toast.makeText(this, "Selecciona región y comuna", Toast.LENGTH_SHORT).show()
            return
        }

        registerUser(name, email, password, selectedRegion!!, selectedComuna!!, address)
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
                    address = address
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