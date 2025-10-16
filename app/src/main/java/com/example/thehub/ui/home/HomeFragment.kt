package com.example.thehub.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thehub.R
import com.example.thehub.data.model.Product
import com.example.thehub.databinding.FragmentHomeBinding
import com.example.thehub.di.ServiceLocator
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val adapter = ProductAdapter()
    private val productRepository = ServiceLocator.productRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = adapter

        loadProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadProducts() {
        binding.progressBar.isVisible = true
        viewLifecycleOwner.lifecycleScope.launch {
            // TIPAMOS explícitamente el Result para evitar "Cannot infer type..."
            val result: Result<List<Product>> = runCatching { productRepository.getProducts() }

            result
                .onSuccess { products: List<Product> ->
                    adapter.submitList(products)
                    binding.progressBar.isVisible = false
                }
                .onFailure { error: Throwable ->
                    binding.progressBar.isVisible = false
                    Toast.makeText(
                        requireContext(),
                        getString(
                            R.string.home_products_error,
                            error.message ?: getString(R.string.error_unknown)
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
