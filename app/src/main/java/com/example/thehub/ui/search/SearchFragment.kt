package com.example.thehub.ui.search

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thehub.R
import com.example.thehub.data.remote.RetrofitClient
import com.example.thehub.data.repository.ProductRepository
import com.example.thehub.ui.home.ProductAdapter
import com.example.thehub.ui.home.ProductDetailBottomSheet
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: ProductAdapter

    // Lista de categorías (asegúrate de que coincidan con tu base de datos Xano)
    private val categoriesList = listOf("todos", "cargador", "carcasas", "audio", "otros")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializar Dependencias
        // Usamos .store() para la conexión pública
        val api = RetrofitClient.store()
        val repository = ProductRepository(api)
        viewModel = SearchViewModel(repository)

        // 2. Referencias a las Vistas
        val svSearch = view.findViewById<SearchView>(R.id.svSearch)
        val cgCategories = view.findViewById<ChipGroup>(R.id.cgCategories)
        val rvSearchResults = view.findViewById<RecyclerView>(R.id.rvSearchResults)
        val pbLoading = view.findViewById<ProgressBar>(R.id.pbLoading)

        // 3. Configurar RecyclerView (Grid Vertical)
        // Usamos 'item_product_grid' para el diseño de tarjetas verticales
        adapter = ProductAdapter(layoutRes = R.layout.item_product_grid) { product ->

            // Al hacer clic, abrimos el BottomSheet de detalle
            val bottomSheet = ProductDetailBottomSheet.newInstance(
                product = product,
                onAddToCart = { selectedProduct, quantity ->
                    // Acción al agregar al carrito
                    viewModel.addToCart(selectedProduct, quantity)
                }
            )
            bottomSheet.show(parentFragmentManager, "ProductDetailBottomSheet")
        }

        rvSearchResults.adapter = adapter
        rvSearchResults.layoutManager = GridLayoutManager(requireContext(), 2)

        // 4. Configurar Chips de Categoría (con sombras)
        setupCategories(cgCategories)

        // 5. Observadores (ViewModel -> UI)
        viewModel.products.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            rvSearchResults.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        // 6. Listener del Buscador
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchProducts(query = query ?: "")
                svSearch.clearFocus() // Ocultar teclado
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Opcional: Búsqueda en tiempo real
                // viewModel.searchProducts(query = newText ?: "")
                return false
            }
        })

        // Carga inicial (trae todos los productos)
        viewModel.searchProducts()
    }

    private fun setupCategories(chipGroup: ChipGroup) {
        chipGroup.removeAllViews()

        var allChip: Chip? = null

        for (categoryName in categoriesList) {
            val chip = Chip(requireContext()).apply {
                // Texto capitalizado (ej: "cargador" -> "Cargador")
                text = categoryName.replaceFirstChar { it.uppercase() }
                isCheckable = true
                isClickable = true

                // --- ESTILO VISUAL ---
                setChipBackgroundColorResource(android.R.color.white) // Fondo blanco
                chipStrokeWidth = 0f // Sin borde de línea
                chipCornerRadius = 50f // Muy redondeado

                // --- SOMBRA (ELEVACIÓN) ---
                elevation = 12f // Esto le da el efecto de flotar

                // Si es "todos", lo marcamos por defecto
                if (categoryName == "todos") {
                    isChecked = true
                    allChip = this
                }
            }

            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // Lógica tipo Radio Button (solo uno activo a la vez)
                    chipGroup.clearCheck()
                    buttonView.isChecked = true

                    // Filtrar
                    if (categoryName == "todos") {
                        viewModel.searchProducts(category = null)
                    } else {
                        viewModel.searchProducts(category = categoryName)
                    }
                } else {
                    // Si se desmarca todo, volver a activar "Todos" por defecto
                    if (chipGroup.checkedChipId == View.NO_ID) {
                        allChip?.isChecked = true
                        viewModel.searchProducts(category = null)
                    }
                }
            }

            chipGroup.addView(chip)
        }
    }
}