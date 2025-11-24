package com.example.thehub.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thehub.data.model.CreateOrderRequest
import com.example.thehub.data.model.OrderItem
import com.example.thehub.data.repository.CartRepository
import com.example.thehub.databinding.ActivityCheckoutBinding
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.profile.EditProfileActivity
import com.example.thehub.ui.profile.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val cartRepository: CartRepository = ServiceLocator.cartRepository
    private val checkoutAdapter = CheckoutSummaryAdapter()


    private var hasValidAddress: Boolean = false
    private var currentSubtotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()


        profileViewModel.loadUserProfile()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        binding.rvCheckoutSummary.layoutManager = LinearLayoutManager(this)
        binding.rvCheckoutSummary.adapter = checkoutAdapter
    }

    private fun setupClickListeners() {

        val goToEditProfile = {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnChangeAddress.setOnClickListener { goToEditProfile() }
        binding.btnAddAddress.setOnClickListener { goToEditProfile() }

        binding.btnConfirmOrder.setOnClickListener { handleConfirmOrder() }
    }

    private fun setupObservers() {

        lifecycleScope.launch {
            profileViewModel.userProfile.collectLatest { user ->
                if (user != null && !user.direccion.isNullOrEmpty()) {
                    hasValidAddress = true
                    setupAddressView(user.direccion, user.comuna, user.region)
                } else {
                    hasValidAddress = false
                    showAddAddressView()
                }
            }
        }


        lifecycleScope.launch {
            cartRepository.cartItems.collectLatest { cartItems ->
                checkoutAdapter.submitList(cartItems)
                currentSubtotal = cartRepository.getSubtotal()
                updateTotals(currentSubtotal)

                if (cartItems.isEmpty()) {
                    Toast.makeText(this@CheckoutActivity, "Carrito vacío", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun setupAddressView(direccion: String, comuna: String?, region: String?) {
        binding.addressView.isVisible = true
        binding.noAddressView.isVisible = false

        val texto = "$direccion\n${comuna ?: ""}, ${region ?: ""}"
        binding.tvAddressDetails.text = texto

        binding.btnConfirmOrder.isEnabled = true
    }

    private fun showAddAddressView() {
        binding.addressView.isVisible = false
        binding.noAddressView.isVisible = true
        binding.btnConfirmOrder.isEnabled = false
    }

    private fun updateTotals(subtotal: Double) {
        val shipping = 0.0
        val total = subtotal + shipping

        binding.tvSubtotal.text = String.format(Locale.US, "$%,.0f", subtotal)
        binding.tvShipping.text = "Gratis"
        binding.tvTotal.text = String.format(Locale.US, "$%,.0f", total)
    }

    private fun handleConfirmOrder() {
        if (!hasValidAddress) {
            Toast.makeText(this, "Agrega una dirección de envío", Toast.LENGTH_SHORT).show()
            return
        }


        binding.loadingOverlay.isVisible = true


        lifecycleScope.launch {
            // TODO: Conectar con OrderRepository cuando configures el endpoint de órdenes en Xano
            Toast.makeText(this@CheckoutActivity, "Procesando pedido...", Toast.LENGTH_SHORT).show()
            kotlinx.coroutines.delay(1500) // Simula carga
            binding.loadingOverlay.isVisible = false
            Toast.makeText(this@CheckoutActivity, "¡Pedido Exitoso! (Simulado)", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}