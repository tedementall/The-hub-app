package com.example.thehub.ui.checkout

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thehub.data.model.Address
import com.example.thehub.data.model.CreateOrderRequest
import com.example.thehub.data.model.OrderItem
import com.example.thehub.data.repository.CartRepository
import com.example.thehub.databinding.ActivityCheckoutBinding
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.profile.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    // Reutilizamos el ViewModel de Perfil y el Repositorio de Carrito
    private val profileViewModel: ProfileViewModel by viewModels()
    private val cartRepository: CartRepository = ServiceLocator.cartRepository

    // El repositorio que tiene la llamada a la API
    // (Asumo que tienes un 'OrderRepository' o similar en ServiceLocator)
    // private val orderRepository = ServiceLocator.orderRepository

    private val checkoutAdapter = CheckoutSummaryAdapter()

    private var currentAddress: Address? = null
    private var currentSubtotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Cargar el perfil del usuario (para obtener la dirección)
        profileViewModel.loadUserProfile()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        binding.rvCheckoutSummary.layoutManager = LinearLayoutManager(this)
        binding.rvCheckoutSummary.adapter = checkoutAdapter
    }

    private fun setupClickListeners() {
        binding.btnChangeAddress.setOnClickListener {
            // TODO: Navegar a la pantalla de "Editar Dirección"
            Toast.makeText(this, "Función de cambiar dirección próximamente", Toast.LENGTH_SHORT).show()
        }
        binding.btnAddAddress.setOnClickListener {
            // TODO: Navegar a la pantalla de "Agregar Dirección" (Perfil)
            Toast.makeText(this, "Por favor, agrega una dirección en tu perfil", Toast.LENGTH_LONG).show()
        }

        binding.btnConfirmOrder.setOnClickListener {
            handleConfirmOrder()
        }
    }

    private fun setupObservers() {
        // 1. Observar el perfil del usuario (para la dirección)
        lifecycleScope.launch {
            profileViewModel.userProfile.collectLatest { user ->
                if (user?.direccion != null && user.direccion.calle.isNotEmpty()) {
                    currentAddress = user.direccion
                    setupAddressView(user.direccion)
                } else {
                    currentAddress = null
                    showAddAddressView()
                }
            }
        }

        // 2. Observar el carrito de compras
        lifecycleScope.launch {
            cartRepository.cartItems.collectLatest { cartItems ->
                checkoutAdapter.submitList(cartItems)

                currentSubtotal = cartRepository.getSubtotal()
                updateTotals(currentSubtotal)

                // Si el carrito se vacía, cerramos esta pantalla
                if (cartItems.isEmpty()) {
                    Toast.makeText(this@CheckoutActivity, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun setupAddressView(address: Address) {
        binding.addressView.isVisible = true
        binding.noAddressView.isVisible = false

        // Reutilizamos la lógica de tu ProfileFragment [cite: 21-27]
        val direccionCompleta = buildString {
            if (address.calle.isNotEmpty()) append(address.calle)
            if (address.numero.isNotEmpty()) append(" #${address.numero}")
            if (address.depto.isNotEmpty()) append(", Depto. ${address.depto}")
            if (address.comuna.isNotEmpty() || address.region.isNotEmpty()) {
                append("\n${address.comuna}")
                if (address.comuna.isNotEmpty() && address.region.isNotEmpty()) append(", ")
                append(address.region)
            }
        }
        binding.tvAddressDetails.text = direccionCompleta
        binding.btnConfirmOrder.isEnabled = true
    }

    private fun showAddAddressView() {
        binding.addressView.isVisible = false
        binding.noAddressView.isVisible = true
        // Deshabilitar el botón de confirmar si no hay dirección
        binding.btnConfirmOrder.isEnabled = false
    }

    private fun updateTotals(subtotal: Double) {
        val shipping = 0.0 // Asumimos envío gratis por ahora
        val total = subtotal + shipping

        binding.tvSubtotal.text = String.format(Locale.US, "$%,.0f", subtotal)
        binding.tvShipping.text = if (shipping == 0.0) "Gratis" else String.format(Locale.US, "$%,.0f", shipping)
        binding.tvTotal.text = String.format(Locale.US, "$%,.0f", total)
    }

    private fun handleConfirmOrder() {
        // 1. Validar Dirección
        if (currentAddress == null || currentAddress?.id == null) {
            Toast.makeText(this, "Por favor, agrega una dirección de envío", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Construir la lista de items
        val cartItems = cartRepository.cartItems.value
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val orderItems = cartItems.map { cartProduct ->
            OrderItem(
                productId = cartProduct.product.id!!,
                quantity = cartProduct.quantity,
                price = cartProduct.product.price // Precio unitario
            )
        }

        // 3. Construir el objeto de la petición
        val createOrderRequest = CreateOrderRequest(
            addressId = currentAddress!!.id!!,
            totalAmount = currentSubtotal,
            status = "pending", // o "procesando", lo que tu API espere
            items = orderItems
        )

        // 4. Mostrar carga y llamar a la API
        binding.loadingOverlay.isVisible = true

        // --- ESTA ES LA PARTE QUE DEBES COMPLETAR ---
        // Necesitas un 'OrderRepository' que llame a 'XanoMainApi.createOrder'

        /*
        viewModelScope.launch {
            try {
                // val newOrder = orderRepository.createOrder(createOrderRequest)

                // SI LA LLAMADA ES EXITOSA:
                Toast.makeText(this@CheckoutActivity, "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show()

                // Limpiar el carrito (esto lo notificará al CartFragment también)
                cartRepository.clearCart() // <-- Necesitarás crear esta función en CartRepository

                // TODO: Navegar a una pantalla de "Orden Exitosa"
                finish() // Por ahora, solo cerramos

            } catch (e: Exception) {
                binding.loadingOverlay.isVisible = false
                Toast.makeText(this@CheckoutA, "Error al crear el pedido: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        */

        // --- QUITA ESTE CÓDIGO DE PRUEBA CUANDO CONECTES LA API ---
        Toast.makeText(this, "TODO: Conectar API para crear orden", Toast.LENGTH_LONG).show()
        binding.loadingOverlay.isVisible = false
        // --- FIN DEL CÓDIGO DE PRUEBA ---
    }
}