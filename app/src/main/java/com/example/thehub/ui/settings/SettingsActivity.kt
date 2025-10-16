package com.example.thehub.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thehub.R
import com.example.thehub.ui.settings.addproduct.AddProductActivity
import com.example.thehub.ui.login.LoginActivity
import com.example.thehub.utils.TokenStore
import com.google.android.material.button.MaterialButton

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)
        val rowAddProduct = findViewById<View>(R.id.rowAddProduct)
        val rowScanCode = findViewById<View>(R.id.rowScanCode)
        val rowEditProfile = findViewById<View>(R.id.rowEditProfile)
        val rowNotifications = findViewById<View>(R.id.rowNotifications)
        val rowPrivacy = findViewById<View>(R.id.rowPrivacy)

        btnLogout.setOnClickListener {
            TokenStore.clear(this)
            val intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
            finish()
        }

        val comingSoonListener = View.OnClickListener {
            Toast.makeText(this, getString(R.string.settings_coming_soon), Toast.LENGTH_SHORT)
                .show()
        }

        rowEditProfile.setOnClickListener(comingSoonListener)
        rowNotifications.setOnClickListener(comingSoonListener)
        rowPrivacy.setOnClickListener(comingSoonListener)
        rowScanCode.setOnClickListener(comingSoonListener)

        rowAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }
    }
}
