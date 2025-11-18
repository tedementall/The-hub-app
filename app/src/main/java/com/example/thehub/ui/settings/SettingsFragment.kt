package com.example.thehub.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.thehub.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Inyectamos el nuevo ViewModel
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupObservers()

        // Verificar el rol al cargar la pantalla
        viewModel.checkUserRole()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isAdmin.collect { isAdmin ->
                // Aquí ocurre la magia:
                // Si es admin, VISIBLE. Si no, GONE (desaparece y no ocupa espacio)
                binding.cardProductOptions.isVisible = isAdmin
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            // ... tus otros listeners de notificaciones, privacidad, etc. ...

            // Listener para agregar productos (solo funcionará si es visible)
            btnAddProduct.setOnClickListener {
                startActivity(Intent(requireContext(), AddProductActivity::class.java))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}