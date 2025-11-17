package com.example.thehub.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.thehub.R
import com.example.thehub.data.repository.CartRepository
import com.example.thehub.databinding.FragmentCartBinding
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.checkout.CheckoutActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

// Usar el nuevo layout 'fragment_cart.xml'
class CartFragment : Fragment(R.layout.fragment_cart) {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    // Accedemos al repositorio desde ServiceLocator
    private val cartRepository: CartRepository = ServiceLocator.cartRepository

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeCart()

        // Conectar el botón para ir a la nueva Activity de Checkout
        binding.btnCheckout.setOnClickListener {
            // Asegurarnos de que el carrito no esté vacío
            if (cartRepository.cartItems.value.isNotEmpty()) {
                // Iniciar la nueva actividad de Checkout
                val intent = Intent(requireContext(), CheckoutActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChange = { productId, newQuantity ->
                cartRepository.updateItemQuantity(productId, newQuantity)
            },
            onRemoveItem = { productId ->
                cartRepository.removeItemFromCart(productId)
            }
        )
        binding.rvCartItems.adapter = cartAdapter
    }

    private fun observeCart() {
        // Observamos el StateFlow del repositorio
        viewLifecycleOwner.lifecycleScope.launch {
            cartRepository.cartItems.collectLatest { cartItems ->
                // Actualizar el adapter
                cartAdapter.submitList(cartItems)

                // Actualizar la vista de "vacío" (usando el estilo opaco)
                binding.emptyView.isVisible = cartItems.isEmpty()
                binding.cardTotal.isVisible = cartItems.isNotEmpty()
                binding.rvCartItems.isVisible = cartItems.isNotEmpty()

                // Calcular y mostrar el subtotal
                val subtotal = cartRepository.getSubtotal()
                val subtotalFormatted = String.format(Locale.US, "%,.0f", subtotal)
                binding.tvSubtotal.text = "$$subtotalFormatted"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}