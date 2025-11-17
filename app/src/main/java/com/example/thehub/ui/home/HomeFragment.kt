package com.example.thehub.ui.home

// --- CAMBIO: Ya no necesitamos Intent, pero sí ServiceLocator ---
// import android.content.Intent // Ya no se usa
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
import com.example.thehub.di.ServiceLocator // <-- AÑADIR ESTE IMPORT
// Ya no importamos la Activity, el BottomSheet está en el mismo paquete.
// import com.example.thehub.ui.detail.ProductDetailActivity // Ya no se usa
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // --- CAMBIO AQUÍ ---
    // Actualizamos la acción de clic del adapter.
    // Ahora, en lugar de abrir una Activity, muestra tu ProductDetailBottomSheet.
    private val adapter = ProductAdapter { product ->

        // 1. Definimos la acción que se ejecutará CUANDO se presione "Agregar al carrito"
        //    dentro del BottomSheet.
        val addToCartAction: (Product, Int) -> Unit = { selectedProduct, quantity ->

            // 2. Usamos el CartRepository que creamos en el ServiceLocator
            ServiceLocator.cartRepository.addProductToCart(selectedProduct, quantity)
        }

        // 3. Creamos la instancia del BottomSheet usando tu método 'newInstance'
        val bottomSheet = ProductDetailBottomSheet.newInstance(
            product = product,
            onAddToCart = addToCartAction // <-- Le pasamos la acción
        )

        // 4. Mostramos el BottomSheet
        bottomSheet.show(childFragmentManager, "ProductDetailBottomSheetTag")
    }
    // --- FIN DEL CAMBIO ---

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
        binding.rvProducts.adapter = adapter // El adapter ya configurado se usa aquí

        loadProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadProducts() {
        binding.progressBar.isVisible = true
        viewLifecycleOwner.lifecycleScope.launch {

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