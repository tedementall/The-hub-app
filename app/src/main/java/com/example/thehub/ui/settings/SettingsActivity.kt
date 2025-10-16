package com.example.thehub.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.thehub.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Bottom Navigation: marca Ajustes
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.menu_settings
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> { finish(); true } // vuelve a Home
                R.id.menu_search -> { msg("Buscar (próximamente)"); true }
                R.id.menu_cart -> { msg("Carrito (próximamente)"); true }
                R.id.menu_profile -> { msg("Perfil (próximamente)"); true }
                R.id.menu_settings -> true
                else -> false
            }
        }

        // Clicks
        findViewById<LinearLayout>(R.id.rowEditProfile).setOnClickListener {
            msg("Editar perfil (próximamente)")
        }
        findViewById<LinearLayout>(R.id.rowNotifications).setOnClickListener {
            msg("Notificaciones (próximamente)")
        }
        findViewById<LinearLayout>(R.id.rowPrivacy).setOnClickListener {
            msg("Privacidad (próximamente)")
        }

        // Agregar productos
        findViewById<LinearLayout>(R.id.rowAddProduct).setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        // Cerrar sesión
        findViewById<LinearLayout>(R.id.rowLogout).setOnClickListener {
            // TODO: limpiar sesión/tokens
            msg("Sesión cerrada (demo)")
            // startActivity(Intent(this, LoginActivity::class.java))
            // finishAffinity()
        }
    }

    private fun msg(text: String) {
        Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show()
    }
}
