package com.example.thehub.ui.settings

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.thehub.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView

class AddProductActivity : AppCompatActivity() {

    private val pickedUris = mutableListOf<Uri>()

    private val pickImages =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            pickedUris.clear()
            val tv = findViewById<TextView>(R.id.tvImageStatus)
            if (!uris.isNullOrEmpty()) {
                pickedUris.addAll(uris)
                tv.text = "${pickedUris.size} imagen(es)"
            } else {
                tv.text = "Sin imágenes"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        // Bottom bar (marca Ajustes)
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.menu_settings
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> { finish(); true }
                R.id.menu_search -> { toast("Buscar (próximamente)"); true }
                R.id.menu_cart -> { toast("Carrito (próximamente)"); true }
                R.id.menu_profile -> { toast("Perfil (próximamente)"); true }
                R.id.menu_settings -> true
                else -> false
            }
        }

        // Seleccionar imágenes
        findViewById<View>(R.id.cardPickImages).setOnClickListener {
            pickImages.launch(arrayOf("image/*"))
        }

        // Guardar (demo + validación)
        findViewById<MaterialButton>(R.id.btnAddProduct).setOnClickListener {
            val name = findViewById<TextInputEditText>(R.id.etName).text?.toString()?.trim().orEmpty()
            val sdesc = findViewById<TextInputEditText>(R.id.etShortDesc).text?.toString()?.trim().orEmpty()
            val ldesc = findViewById<TextInputEditText>(R.id.etLongDesc).text?.toString()?.trim().orEmpty()
            val priceStr = findViewById<TextInputEditText>(R.id.etPrice).text?.toString()?.trim().orEmpty()
            val stockStr = findViewById<TextInputEditText>(R.id.etStock).text?.toString()?.trim().orEmpty()
            val minStockStr = findViewById<TextInputEditText>(R.id.etMinStock).text?.toString()?.trim().orEmpty()

            if (name.isEmpty()) { toast("Falta el nombre"); return@setOnClickListener }
            val price = priceStr.toDoubleOrNull()
            if (price == null) { toast("Precio inválido"); return@setOnClickListener }

            val stock = stockStr.toIntOrNull() ?: 0
            val minStock = minStockStr.toIntOrNull() ?: 0

            // TODO: aquí llamas a tu repositorio para crear el producto (Xano/Room/etc.)
            toast("Producto listo para enviar:\n$name - $$price ($stock/${minStock})\nImágenes: ${pickedUris.size}")
            finish()
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
