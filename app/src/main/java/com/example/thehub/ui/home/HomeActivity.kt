package com.example.thehub.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thehub.R
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var bottomBar: BottomNavigationView

    private val adapter = ProductAdapter()
    private val productRepository = ServiceLocator.productRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Deja que el sistema pinte la barra de navegación (sin edge-to-edge)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        // La barra de navegación del sistema en blanco
        window.navigationBarColor = Color.WHITE

        setContentView(R.layout.activity_home)

        recycler    = findViewById(R.id.rvProducts)
        progressBar = findViewById(R.id.progressBar)
        bottomBar   = findViewById(R.id.bottomNav)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Bottom bar (solo UI, el único que “hace algo” es Ajustes → Cerrar sesión)
        bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_settings -> {
                    showSettingsDialog()
                    true
                }
                else -> {
                    Toast.makeText(this, "Sección en desarrollo ✨", Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }

        // Carga inicial de productos
        update()
    }

    /** Carga/refresca la lista de productos desde Xano */
    private fun update() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val products = productRepository.getProducts()
                adapter.submitList(products)
            } catch (e: Exception) {
                Toast.makeText(
                    this@HomeActivity,
                    "Error cargando productos: ${e.message ?: "desconocido"}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Ajustes")
            .setItems(arrayOf("Cerrar sesión")) { dialog, which ->
                if (which == 0) {
                    // Si tienes tu TokenStore, descomenta estas dos líneas:
                    // val store = com.example.thehub.utils.TokenStore(this)
                    // store.clear()

                    // Vuelve a la pantalla de login
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
