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
import com.example.thehub.ui.settings.AdminOrderListActivity
import com.example.thehub.ui.settings.AdminProductListActivity
import com.example.thehub.ui.settings.AdminUserListActivity
import com.example.thehub.ui.blog.AddNewsActivity
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

        viewModel.loadUserProfile()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collect { user ->
                user?.let {
                    binding.apply {
                        tvUserName.text = it.nombre
                        tvUserEmail.text = it.correo
                        tvUserRole.text = if (it.esAdministrador) "Administrador" else "Usuario"

                        cardAdminOptions.isVisible = it.esAdministrador

                        if (!it.direccion.isNullOrEmpty()) {
                            val sb = StringBuilder()
                            sb.append(it.direccion)
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {

            btnSettings.setOnClickListener {
                try {
                    val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.slide_in_right)
                        .setExitAnim(R.anim.slide_out_left)
                        .setPopEnterAnim(R.anim.slide_in_left)
                        .setPopExitAnim(R.anim.slide_out_right)
                        .setPopUpTo(R.id.navigation_profile, inclusive = true)
                        .build()

                    findNavController().navigate(R.id.navigation_settings, null, navOptions)

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error al navegar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            btnMyOrders.setOnClickListener {
                val user = viewModel.userProfile.value
                if (user != null) {
                    val intent = Intent(requireContext(), MyOrdersActivity::class.java)
                    intent.putExtra("USER_ID", user.id)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Esperando datos del perfil...", Toast.LENGTH_SHORT).show()
                    viewModel.loadUserProfile()
                }
            }

            btnAddProduct.setOnClickListener {
                val intent = Intent(requireContext(), AddProductActivity::class.java)
                startActivity(intent)
            }

            btnManageProducts.setOnClickListener {
                val intent = Intent(requireContext(), AdminProductListActivity::class.java)
                startActivity(intent)
            }

            btnManageUsers.setOnClickListener {
                val intent = Intent(requireContext(), AdminUserListActivity::class.java)
                startActivity(intent)
            }

            btnAddNews.setOnClickListener {
                val intent = Intent(requireContext(), AddNewsActivity::class.java)
                startActivity(intent)
            }

            btnManageOrders.setOnClickListener {
                val intent = Intent(requireContext(), AdminOrderListActivity::class.java)
                startActivity(intent)
            }

            btnEditProfile.setOnClickListener {
                val currentUser = viewModel.userProfile.value
                val intent = Intent(requireContext(), EditProfileActivity::class.java)
                if (currentUser != null) {
                    intent.putExtra("NAME", currentUser.nombre)
                    intent.putExtra("ADDRESS", currentUser.direccion)
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