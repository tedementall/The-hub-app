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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: ProductAdapter

    // Lista de categorías incluyendo "Todos" al principio
    // Asegúrate de que "cargador", "carcasas", etc., coincidan con tu Xano
    private val categoriesList = listOf("todos", "cargador", "carcasas", "audio", "otros")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializar Dependencias
        val api = RetrofitClient.store() // Usamos tu cliente existente
        val repository = ProductRepository(api)
        viewModel = SearchViewModel(repository)

        // 2. Referencias a las Vistas
        val svSearch = view.findViewById<SearchView>(R.id.svSearch)
        val cgCategories = view.findViewById<ChipGroup>(R.id.cgCategories)
        val rvSearchResults = view.findViewById<RecyclerView>(R.id.rvSearchResults)
        val pbLoading = view.findViewById<ProgressBar>(R.id.pbLoading)

        // 3. Configurar RecyclerView (Grid de 2 columnas)
        // IMPORTANTE: Usamos el layout 'item_product_grid' para el diseño vertical
        adapter = ProductAdapter(layoutRes = R.layout.item_product_grid) { product ->
            // Aquí iría la navegación al detalle del producto
            // Ej: findNavController().navigate(...)
        }

        rvSearchResults.adapter = adapter
        rvSearchResults.layoutManager = GridLayoutManager(requireContext(), 2)

        // 4. Configurar Chips de Categoría
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
                // Opcional: Si quieres búsqueda en tiempo real descomenta esto
                // viewModel.searchProducts(query = newText ?: "")
                return false
            }
        })

        // Carga inicial (Trae todos los productos al abrir)
        viewModel.searchProducts()
    }

    private fun setupCategories(chipGroup: ChipGroup) {
        chipGroup.removeAllViews()

        // Guardamos referencia al chip "Todos" para poder reactivarlo si es necesario
        var allChip: Chip? = null

        for (categoryName in categoriesList) {
            val chip = Chip(requireContext()).apply {
                // Capitalizar primera letra (ej: "cargador" -> "Cargador")
                text = categoryName.replaceFirstChar { it.uppercase() }
                isCheckable = true
                isClickable = true

                // ESTILO VISUAL: Blanco y limpio (como en tu diseño de Inicio)
                setChipBackgroundColorResource(android.R.color.white)
                chipStrokeWidth = 0f // Sin borde negro
                // Puedes ajustar el radio si quieres más o menos redondeado
                chipCornerRadius = 50f

                // Si es "todos", lo marcamos por defecto al iniciar
                if (categoryName == "todos") {
                    isChecked = true
                    allChip = this
                }
            }

            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // Comportamiento tipo "Radio Button": Solo uno activo a la vez
                    chipGroup.clearCheck()
                    buttonView.isChecked = true

                    if (categoryName == "todos") {
                        // Si eligió "Todos", enviamos null para limpiar el filtro
                        viewModel.searchProducts(category = null)
                    } else {
                        // Si eligió una categoría, filtramos por ella
                        viewModel.searchProducts(category = categoryName)
                    }
                } else {
                    // Si el usuario desmarca manualmente el chip activo...
                    // ...automáticamente volvemos a marcar "Todos" para no quedarnos sin selección
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