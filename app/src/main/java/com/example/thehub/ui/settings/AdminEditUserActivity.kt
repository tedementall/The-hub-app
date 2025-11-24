package com.example.thehub.ui.settings

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.thehub.data.model.User
import com.example.thehub.databinding.ActivityAdminEditUserBinding
import com.example.thehub.di.ServiceLocator
import kotlinx.coroutines.launch

class AdminEditUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminEditUserBinding
    private val userRepository = ServiceLocator.userRepository
    private var userToEdit: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userToEdit = intent.getParcelableExtra("extra_user")

        setupUI()
        populateData()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        val roles = listOf("Administrador", "Cliente")
        val roleAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        binding.acRole.setAdapter(roleAdapter)

        val statuses = listOf("Activo", "Bloqueado")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statuses)
        binding.acStatus.setAdapter(statusAdapter)

        binding.btnSaveChanges.setOnClickListener {
            saveUserChanges()
        }
    }

    private fun populateData() {
        userToEdit?.let { user ->
            binding.etName.setText(user.nombre)
            binding.etEmail.setText(user.correo)

            val roleText = if (user.tipoUsuario == "admin") "Administrador" else "Cliente"
            binding.acRole.setText(roleText, false)

            val statusText = if (user.estado == "activo") "Activo" else "Bloqueado"
            binding.acStatus.setText(statusText, false)
        }
    }

    private fun saveUserChanges() {
        val newName = binding.etName.text.toString()
        val roleSelection = binding.acRole.text.toString()
        val statusSelection = binding.acStatus.text.toString()

        val dbRole = if (roleSelection == "Administrador") "admin" else "cliente"
        val dbStatus = if (statusSelection == "Activo") "activo" else "bloqueado"

        val updates = mapOf(
            "name" to newName,
            "user_type" to dbRole,
            "status" to dbStatus
        )

        lifecycleScope.launch {
            try {
                if (userToEdit != null) {
                    userRepository.adminUpdateUser(userToEdit!!.id, updates)
                    Toast.makeText(this@AdminEditUserActivity, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminEditUserActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}