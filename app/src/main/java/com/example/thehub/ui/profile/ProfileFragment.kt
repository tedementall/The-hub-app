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

        // Cargar datos del perfil
        viewModel.loadUserProfile()
    }

    private fun setupObservers() {
        // Observar el estado de carga
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        }

        // Observar los datos del usuario
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collect { user ->
                user?.let {
                    binding.apply {
                        tvUserName.text = it.nombre
                        tvUserEmail.text = it.correo

                        // Mostrar rol del usuario
                        tvUserRole.text = if (it.esAdministrador) "Administrador" else "Usuario"

                        // Mostrar dirección si existe
                        if (it.direccion != null) {
                            val direccionCompleta = buildString {
                                if (it.direccion.calle.isNotEmpty()) {
                                    append(it.direccion.calle)
                                }
                                if (it.direccion.numero.isNotEmpty()) {
                                    append(" #${it.direccion.numero}")
                                }
                                if (it.direccion.depto.isNotEmpty()) {
                                    append(", Depto. ${it.direccion.depto}")
                                }
                                if (it.direccion.comuna.isNotEmpty() || it.direccion.region.isNotEmpty()) {
                                    append("\n${it.direccion.comuna}")
                                    if (it.direccion.comuna.isNotEmpty() && it.direccion.region.isNotEmpty()) {
                                        append(", ")
                                    }
                                    append(it.direccion.region)
                                }
                            }
                            tvUserAddress.text = if (direccionCompleta.isNotEmpty()) {
                                direccionCompleta
                            } else {
                                "No hay dirección registrada"
                            }
                        } else {
                            tvUserAddress.text = "No hay dirección registrada"
                        }

                        // Mostrar teléfono si existe
                        if (!it.telefono.isNullOrEmpty()) {
                            tvUserPhone.text = it.telefono
                            phoneLayout.visibility = View.VISIBLE
                        } else {
                            phoneLayout.visibility = View.GONE
                        }

                        // Mostrar RUT si existe
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

        // Observar errores
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            // Botón para editar perfil
            btnEditProfile.setOnClickListener {
                Toast.makeText(requireContext(), "Función de edición próximamente", Toast.LENGTH_SHORT).show()
            }

            // Botón para cerrar sesión
            btnLogout.setOnClickListener {
                viewModel.logout()
                // Navegar al login
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