package com.example.thehub.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thehub.R
import com.example.thehub.di.ServiceLocator
import com.example.thehub.ui.login.LoginActivity
import com.example.thehub.utils.TokenStore
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val adapter = ProductAdapter()

    // Repos
    private val productRepository = ServiceLocator.productRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Views
        recycler = findViewById(R.id.rvProducts)
        progressBar = findViewById(R.id.progressBar)

        // Lista
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // BottomNavigation
        findViewById<BottomNavigationView?>(R.id.bottomNav)?.let { bottomBar ->
            // Evitar que quede pegada a la barra de navegación
            ViewCompat.setOnApplyWindowInsetsListener(bottomBar) { v, insets ->
                val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(
                    v.paddingLeft,
                    v.paddingTop,
                    v.paddingRight,
                    maxOf(v.paddingBottom, sys.bottom)
                )
                insets
            }

            bottomBar.selectedItemId = R.id.menu_home
            bottomBar.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_settings -> {
                        showSettingsDialog()
                        true
                    }
                    else -> {
                        // Por ahora mantenemos a Home como única pantalla funcional
                        bottomBar.selectedItemId = R.id.menu_home
                        true
                    }
                }
            }
        }

        // Carga inicial
        update()
    }

    override fun onResume() {
        super.onResume()
        // Si no hay token, vuelve a Login
        if (TokenStore.read(this).isNullOrEmpty()) {
            val i = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(i)
            finish()
        }
    }

    /** Descarga/actualiza la lista de productos desde Xano */
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

    /** Popup de ajustes con acción de Cerrar sesión */
    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Ajustes")
            .setItems(arrayOf("Cerrar sesión")) { _, which ->
                if (which == 0) {
                    // 1) Borrar el token/estado de sesión
                    TokenStore.clear(this)

                    // 2) Ir a Login limpiando el back stack
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)

                    // 3) Cerrar Home
                    finish()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
