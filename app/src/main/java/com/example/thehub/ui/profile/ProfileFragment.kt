package com.example.thehub.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.thehub.databinding.FragmentProfileBinding
import com.example.thehub.ui.login.LoginActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
        viewModel.loadUserProfile()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collect { user ->
                user?.let {
                    binding.apply {
                        tvUserName.text = it.nombre
                        tvUserEmail.text = it.correo
                        tvUserRole.text = if (it.esAdministrador) "Administrador" else "Usuario"

                        // --- LÓGICA DE DIRECCIÓN ACTUALIZADA ---
                        if (!it.direccion.isNullOrEmpty()) {
                            val sb = StringBuilder()
                            sb.append(it.direccion)
                            if (!it.comuna.isNullOrEmpty()) sb.append("\n${it.comuna}")
                            if (!it.region.isNullOrEmpty()) sb.append(", ${it.region}")

                            tvUserAddress.text = sb.toString()
                        } else {
                            tvUserAddress.text = "No hay dirección registrada"
                        }
                        // ---------------------------------------

                        if (!it.telefono.isNullOrEmpty()) {
                            tvUserPhone.text = it.telefono
                            phoneLayout.visibility = View.VISIBLE
                        } else {
                            phoneLayout.visibility = View.GONE
                        }

                        if (!it.rut.isNullOrEmpty()) {
                            tvUserRut.text = it.rut
                            rutLayout.visibility = View.VISIBLE
                        } else {
                            rutLayout.visibility = View.GONE
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            // Botón EDITAR PERFIL conectado
            btnEditProfile.setOnClickListener {
                val currentUser = viewModel.userProfile.value
                val intent = Intent(requireContext(), EditProfileActivity::class.java)

                // Pasamos datos actuales para rellenar
                if (currentUser != null) {
                    intent.putExtra("NAME", currentUser.nombre)
                    intent.putExtra("ADDRESS", currentUser.direccion)
                    // Agrega comuna/region si quieres pre-llenarlos en el futuro
                }
                startActivity(intent)
            }

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