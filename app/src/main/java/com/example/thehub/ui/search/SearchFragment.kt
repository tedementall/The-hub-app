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


    private val categoriesList = listOf("todos", "cargador", "carcasas", "audio", "otros")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val api = RetrofitClient.store()
        val repository = ProductRepository(api)
        viewModel = SearchViewModel(repository)


        val svSearch = view.findViewById<SearchView>(R.id.svSearch)
        val cgCategories = view.findViewById<ChipGroup>(R.id.cgCategories)
        val rvSearchResults = view.findViewById<RecyclerView>(R.id.rvSearchResults)
        val pbLoading = view.findViewById<ProgressBar>(R.id.pbLoading)


        adapter = ProductAdapter(layoutRes = R.layout.item_product_grid) { product ->


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


        setupCategories(cgCategories)


        viewModel.products.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            rvSearchResults.visibility = if (isLoading) View.GONE else View.VISIBLE
        }


        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchProducts(query = query ?: "")
                svSearch.clearFocus() // Ocultar teclado
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return false
            }
        })


        viewModel.searchProducts()
    }

    private fun setupCategories(chipGroup: ChipGroup) {
        chipGroup.removeAllViews()

        var allChip: Chip? = null

        for (categoryName in categoriesList) {
            val chip = Chip(requireContext()).apply {

                text = categoryName.replaceFirstChar { it.uppercase() }
                isCheckable = true
                isClickable = true


                setChipBackgroundColorResource(android.R.color.white) // Fondo blanco
                chipStrokeWidth = 0f
                chipCornerRadius = 50f


                elevation = 12f


                if (categoryName == "todos") {
                    isChecked = true
                    allChip = this
                }
            }

            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {

                    chipGroup.clearCheck()
                    buttonView.isChecked = true


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