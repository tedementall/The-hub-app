package com.example.thehub.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thehub.R
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val adapter = ProductAdapter()

    // Repo
    private val productRepository = ServiceLocator.productRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recycler = findViewById(R.id.rvProducts)
        progressBar = findViewById(R.id.progressBar)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav) // <- usa el id del XML

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Evita que quede pegado a la barra de gestos
        bottomNav?.let { bar ->
            ViewCompat.setOnApplyWindowInsetsListener(bar) { v, insets ->
                val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, v.paddingBottom + sys.bottom)
                insets
            }

            bar.menu.findItem(R.id.menu_home).isChecked = true
            bar.setOnItemReselectedListener { /* no-op */ }
            bar.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_home -> true
                    R.id.menu_search -> { Toast.makeText(this, "Buscar (próximamente)", Toast.LENGTH_SHORT).show(); true }
                    R.id.menu_cart   -> { Toast.makeText(this, "Carrito (próximamente)", Toast.LENGTH_SHORT).show(); true }
                    R.id.menu_profile-> { Toast.makeText(this, "Perfil (próximamente)", Toast.LENGTH_SHORT).show(); true }
                    R.id.menu_settings -> {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
        }

        update()
    }

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

}
