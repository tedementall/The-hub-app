package com.example.thehub.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thehub.data.model.Product
import com.example.thehub.databinding.ActivityAdminProductListBinding
import com.example.thehub.di.ServiceLocator
import com.example.thehub.utils.TokenStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AdminProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminProductListBinding
    private val productRepository = ServiceLocator.productRepository
    private lateinit var adapter: AdminProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        loadProducts()
    }

    override fun onResume() {
        super.onResume()

        loadProducts()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = AdminProductAdapter(
            products = emptyList(),
            onEditClick = { product ->

                val intent = Intent(this, AddProductActivity::class.java)
                intent.putExtra("extra_product", product)
                startActivity(intent)
            },
            onDeleteClick = { product ->
                showDeleteConfirmation(product)
            }
        )
        binding.rvProducts.adapter = adapter
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            try {

                val products = productRepository.getProducts(limit = 100, offset = 0, q = null, category = null)
                adapter.updateList(products)
            } catch (e: Exception) {
                Toast.makeText(this@AdminProductListActivity, "Error cargando inventario", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showDeleteConfirmation(product: Product) {
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Eliminar ${product.name}?")
            .setMessage("Esta acción eliminará el producto del catálogo permanentemente.")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                deleteProduct(product)
            }
            .show()
    }

    private fun deleteProduct(product: Product) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            val token = TokenStore.read(this@AdminProductListActivity) ?: ""
            val success = productRepository.deleteProduct(token, product.id!!)

            if (success) {
                Toast.makeText(this@AdminProductListActivity, "Producto eliminado", Toast.LENGTH_SHORT).show()
                loadProducts() // Recargar lista
            } else {
                Toast.makeText(this@AdminProductListActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}