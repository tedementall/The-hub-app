package com.example.thehub.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thehub.databinding.ActivityRegisterBinding
// Asegúrate de que este binding apunte a tu layout 'activity_register.xml'
// Si creaste uno nuevo, cambia el nombre aquí.

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // Adaptamos visualmente el layout de registro para que parezca de edición
        binding.tvTitle.text = "Editar Perfil"
        binding.tvSubtitle.text = "Actualiza tu información de envío"
        binding.btnRegister.text = "Guardar Cambios"
        binding.tvLoginLink.text = "" // Ocultamos el link de "Iniciar sesión"

        // Ocultamos campos que quizás no quieras editar (como email o pass)
        // Opcional: binding.etEmail.isEnabled = false
        // Opcional: binding.etPassword.visibility = View.GONE

        binding.btnBack.setOnClickListener { finish() }

        // PRE-CARGAR DATOS (Recibidos desde ProfileFragment)
        val currentName = intent.getStringExtra("NAME")
        val currentAddress = intent.getStringExtra("ADDRESS")

        if (!currentName.isNullOrEmpty()) binding.etName.setText(currentName)
        if (!currentAddress.isNullOrEmpty()) binding.etAddressDetail.setText(currentAddress)

        // TODO: Configurar de nuevo los Dropdowns de Región/Comuna aquí
        // (puedes copiar la lógica de RegisterActivity si la necesitas)

        binding.btnRegister.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        // Como aún no tenemos endpoint 'Update Profile' en Xano:
        Toast.makeText(this, "Función de guardar en construcción (Backend pendiente)", Toast.LENGTH_SHORT).show()

        // Cerramos para volver al perfil
        finish()
    }
}