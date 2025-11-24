package com.example.thehub.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.thehub.data.model.EditUserRequest
import com.example.thehub.databinding.ActivityRegisterBinding
import com.example.thehub.di.ServiceLocator
import com.example.thehub.utils.ChileLocationHelper
import com.example.thehub.utils.TokenStore
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var selectedRegion: String? = null
    private var selectedComuna: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupDropdowns()
        loadCurrentData()
    }

    private fun setupUI() {

        binding.tvTitle.text = "Editar Perfil"
        binding.tvSubtitle.text = "Actualiza tu información de envío"
        binding.btnRegister.text = "Guardar Cambios"


        binding.tvLoginLink.text = ""
        binding.tvLoginLink.isEnabled = false


        binding.etEmail.isEnabled = false
        binding.etEmail.alpha = 0.6f


        binding.tilPassword.visibility = View.GONE


        binding.btnBack.setOnClickListener { finish() }

        binding.btnRegister.setOnClickListener {
            saveChanges()
        }
    }

    private fun setupDropdowns() {
        val regions = ChileLocationHelper.getRegions()
        val regionAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, regions)
        binding.autoCompleteRegion.setAdapter(regionAdapter)

        binding.autoCompleteRegion.setOnItemClickListener { _, _, position, _ ->
            val regionName = regionAdapter.getItem(position).toString()
            selectedRegion = regionName

            selectedComuna = null
            binding.autoCompleteComuna.text = null
            binding.tilComuna.isEnabled = true

            loadComunasForRegion(regionName)
        }

        binding.autoCompleteComuna.setOnItemClickListener { adapterView, _, position, _ ->
            selectedComuna = adapterView.getItemAtPosition(position).toString()
        }
    }

    private fun loadComunasForRegion(region: String) {
        val comunas = ChileLocationHelper.getComunas(region)
        val comunaAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, comunas)
        binding.autoCompleteComuna.setAdapter(comunaAdapter)
    }

    private fun loadCurrentData() {
        val currentName = intent.getStringExtra("NAME")
        val currentAddress = intent.getStringExtra("ADDRESS")
        val currentRegion = intent.getStringExtra("REGION")
        val currentComuna = intent.getStringExtra("COMUNA")

        if (!currentName.isNullOrEmpty()) binding.etName.setText(currentName)
        if (!currentAddress.isNullOrEmpty()) binding.etAddressDetail.setText(currentAddress)

        // Pre-carga de selectores
        if (!currentRegion.isNullOrEmpty()) {
            selectedRegion = currentRegion
            binding.autoCompleteRegion.setText(currentRegion, false)
            binding.tilComuna.isEnabled = true
            loadComunasForRegion(currentRegion)

            if (!currentComuna.isNullOrEmpty()) {
                selectedComuna = currentComuna
                binding.autoCompleteComuna.setText(currentComuna, false)
            }
        }
    }

    private fun saveChanges() {
        val name = binding.etName.text.toString().trim()
        val address = binding.etAddressDetail.text.toString().trim()

        if (name.isEmpty() || address.isEmpty() || selectedRegion == null || selectedComuna == null) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val token = TokenStore.read(this@EditProfileActivity)
                if (token.isNullOrEmpty()) {
                    Toast.makeText(this@EditProfileActivity, "Sesión expirada", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                val request = EditUserRequest(
                    name = name,
                    region = selectedRegion!!,
                    comuna = selectedComuna!!,
                    address = address
                )

                val response = ServiceLocator.authRepository.updateProfile(token, request)

                if (response != null) {
                    Toast.makeText(this@EditProfileActivity, "¡Perfil actualizado!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProfileActivity, "Error al actualizar.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}