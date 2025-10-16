package com.example.thehub.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.thehub.R
import com.example.thehub.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura el NavHostFragment y el NavController para la navegación
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Vincula la barra de navegación inferior con el NavController
        binding.bottomNav.setupWithNavController(navController)

        // Evita que el fragmento se recargue al volver a seleccionar el mismo ítem
        binding.bottomNav.setOnItemReselectedListener { /* no-op */ }

        // Guarda el padding inferior original de la vista antes de aplicar cambios.
        val initialBottomPadding = binding.bottomNav.paddingBottom

        // Escucha los cambios en los "insets" de la ventana (como la barra de sistema).
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { v, insets ->
            // Obtiene la altura de las barras del sistema (como la barra de gestos).
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Aplica el padding, usando siempre el valor INICIAL + la altura de la barra del sistema.
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, initialBottomPadding + systemBars.bottom)

            // Devuelve los insets para que otros elementos también puedan usarlos.
            insets
        }
    }
}