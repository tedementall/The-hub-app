package com.example.thehub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.NavOptions
import com.example.thehub.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setOnItemSelectedListener { item ->
            val currentDestId = navController.currentDestination?.id

            // Si tocamos el ítem de Perfil
            if (item.itemId == R.id.navigation_profile) {
                // Si estamos en Settings, hacemos pop
                if (currentDestId == R.id.navigation_settings) {
                    navController.popBackStack(R.id.navigation_profile, false)
                    return@setOnItemSelectedListener true
                }

                // Si ya estamos en Profile, no hacemos nada
                if (currentDestId == R.id.navigation_profile) {
                    return@setOnItemSelectedListener true
                }
            }

            // Para cualquier otro caso, navegación normal
            // pero SIN guardar estado
            val navOptions = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setRestoreState(false) // No restaurar estado
                .setPopUpTo(
                    navController.graph.startDestinationId,
                    inclusive = false,
                    saveState = false // No guardar estado
                )
                .build()

            try {
                navController.navigate(item.itemId, null, navOptions)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}