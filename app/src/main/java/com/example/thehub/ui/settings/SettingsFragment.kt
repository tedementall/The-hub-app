package com.example.thehub.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.thehub.R
import com.example.thehub.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

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

        binding.rowEditProfile.setOnClickListener {
            showMessage(getString(R.string.settings_edit_profile_coming_soon))
        }
        binding.rowNotifications.setOnClickListener {
            showMessage(getString(R.string.settings_notifications_coming_soon))
        }
        binding.rowNotifications2.setOnClickListener {
            showMessage(getString(R.string.settings_notifications_coming_soon))
        }
        binding.rowPrivacy.setOnClickListener {
            showMessage(getString(R.string.settings_privacy_coming_soon))
        }
        binding.rowAddProduct.setOnClickListener {
            startActivity(Intent(requireContext(), AddProductActivity::class.java))
        }
        binding.rowLogout.setOnClickListener {
            showMessage(getString(R.string.settings_logout_demo))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
