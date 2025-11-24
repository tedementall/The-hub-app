package com.example.thehub.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thehub.R
import com.example.thehub.data.model.Product
import com.example.thehub.databinding.FragmentHomeBinding
import com.example.thehub.di.ServiceLocator
import com.example.thehub.utils.TokenStore
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val productRepository = ServiceLocator.productRepository

    private val adapter = ProductAdapter { product ->
        val addToCartAction: (Product, Int) -> Unit = { selectedProduct, quantity ->
            ServiceLocator.cartRepository.addProductToCart(selectedProduct, quantity)
            Toast.makeText(requireContext(), "Agregado al carrito", Toast.LENGTH_SHORT).show()
        }

        val bottomSheet = ProductDetailBottomSheet.newInstance(
            product = product,
            onAddToCart = addToCartAction
        )
        bottomSheet.show(childFragmentManager, "ProductDetailBottomSheetTag")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupHeader()
        setupRecyclerView()
        loadProducts()
    }

    private fun setupHeader() {

        val userName = TokenStore.getUserName(requireContext()) ?: "Usuario"
        binding.tvUserNameHome.text = "Hola, $userName \uD83D\uDC8B"
    }

    private fun setupRecyclerView() {
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = adapter
        binding.rvProducts.isNestedScrollingEnabled = false
    }

    private fun loadProducts() {
        binding.progressBar.isVisible = true
        viewLifecycleOwner.lifecycleScope.launch {
            val result = runCatching { productRepository.getProducts() }

            result.onSuccess { products ->
                adapter.submitList(products)
                binding.progressBar.isVisible = false
            }.onFailure { error ->
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), "Error al cargar: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}