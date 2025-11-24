package com.example.thehub.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.thehub.R
import com.example.thehub.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            // BOTÓN ATRÁS
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            // Opciones
            rowNotifications.setOnClickListener {
                Toast.makeText(requireContext(), "Configuración de notificaciones", Toast.LENGTH_SHORT).show()
            }
            rowSearch.setOnClickListener {
                Toast.makeText(requireContext(), "Historial de búsqueda", Toast.LENGTH_SHORT).show()
            }
            rowPrivacy.setOnClickListener {
                Toast.makeText(requireContext(), "Configuración de privacidad", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}