package com.example.thehub.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.thehub.R
import com.example.thehub.databinding.FragmentProfileBinding
import com.example.thehub.ui.login.LoginActivity
import com.example.thehub.ui.settings.AddProductActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        setupObservers()
        setupClickListeners()

        // Cargar datos al entrar
        viewModel.loadUserProfile()
    }

    private fun setupObservers() {
        // 1. Loading
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        // 2. Datos del Usuario
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collect { user ->
                user?.let {
                    binding.apply {
                        tvUserName.text = it.nombre
                        tvUserEmail.text = it.correo

                        // Rol
                        tvUserRole.text = if (it.esAdministrador) "Administrador" else "Usuario"

                        // --- LÓGICA DE ADMIN ---
                        // Si es admin, mostramos la tarjeta especial. Si no, se oculta.
                        cardAdminOptions.isVisible = it.esAdministrador
                        // -----------------------

                        // --- DIRECCIÓN ---
                        if (!it.direccion.isNullOrEmpty()) {
                            val sb = StringBuilder()
                            sb.append(it.direccion) // Calle
                            if (!it.comuna.isNullOrEmpty()) sb.append("\n${it.comuna}")
                            if (!it.region.isNullOrEmpty()) sb.append(", ${it.region}")

                            tvUserAddress.text = sb.toString()
                        } else {
                            tvUserAddress.text = "No hay dirección registrada"
                        }
                    }
                }
            }
        }

        // 3. Errores
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {

            // 1. BOTÓN AJUSTES (TUERCA FLOTANTE) CON ANIMACIÓN
            btnSettings.setOnClickListener {
                try {
                    // Configurar la animación de deslizamiento suave
                    val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.slide_in_right)   // Entra desde derecha
                        .setExitAnim(R.anim.slide_out_left)    // Perfil se va a izquierda
                        .setPopEnterAnim(R.anim.slide_in_left) // Perfil vuelve desde izquierda
                        .setPopExitAnim(R.anim.slide_out_right)// Ajustes se va a derecha
                        // CRÍTICO: Esto elimina Profile del backstack cuando vas a Settings
                        .setPopUpTo(R.id.navigation_profile, inclusive = true)
                        .build()

                    findNavController().navigate(R.id.navigation_settings, null, navOptions)

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error al navegar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // 2. BOTÓN AGREGAR PRODUCTO (Dentro de tarjeta Admin)
            btnAddProduct.setOnClickListener {
                val intent = Intent(requireContext(), AddProductActivity::class.java)
                startActivity(intent)
            }

            // 3. BOTÓN EDITAR PERFIL
            btnEditProfile.setOnClickListener {
                val currentUser = viewModel.userProfile.value
                val intent = Intent(requireContext(), EditProfileActivity::class.java)

                // Pasamos datos actuales para rellenar inputs
                if (currentUser != null) {
                    intent.putExtra("NAME", currentUser.nombre)
                    intent.putExtra("ADDRESS", currentUser.direccion)
                }
                startActivity(intent)
            }

            // 4. CERRAR SESIÓN
            btnLogout.setOnClickListener {
                viewModel.logout()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}