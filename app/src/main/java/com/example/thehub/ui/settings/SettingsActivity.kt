package com.example.thehub.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.thehub.R
import com.example.thehub.ui.login.LoginActivity
import com.example.thehub.utils.TokenStore
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SettingsActivity : AppCompatActivity() {

    private val selectImagesLauncher =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            onImagesSelected(uris)
        }

    private val selectedImages = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)
        val btnSelectImages = findViewById<MaterialButton>(R.id.btnSelectImages)
        val btnAddProduct = findViewById<MaterialButton>(R.id.btnAddProduct)
        val tvImagesHint = findViewById<TextView>(R.id.tvImagesHint)
        val etName = findViewById<TextInputEditText>(R.id.etProductName)
        val etDescription = findViewById<TextInputEditText>(R.id.etProductDescription)
        val etPrice = findViewById<TextInputEditText>(R.id.etProductPrice)
        val etStock = findViewById<TextInputEditText>(R.id.etProductStock)

        btnLogout.setOnClickListener {
            TokenStore.clear(this)
            val intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
            finish()
        }

        btnSelectImages.setOnClickListener {
            selectImagesLauncher.launch(arrayOf("image/*"))
        }

        btnAddProduct.setOnClickListener {
            val name = etName.text?.toString()?.trim().orEmpty()
            val description = etDescription.text?.toString()?.trim().orEmpty()
            val price = etPrice.text?.toString()?.trim().orEmpty()
            val stock = etStock.text?.toString()?.trim().orEmpty()

            etName.error = null
            etDescription.error = null
            etPrice.error = null
            etStock.error = null

            var hasError = false

            if (name.isEmpty()) {
                etName.error = getString(R.string.error_required_field)
                hasError = true
            }
            if (description.isEmpty()) {
                etDescription.error = getString(R.string.error_required_field)
                hasError = true
            }
            if (price.isEmpty()) {
                etPrice.error = getString(R.string.error_required_field)
                hasError = true
            }
            if (stock.isEmpty()) {
                etStock.error = getString(R.string.error_required_field)
                hasError = true
            }

            if (!hasError) {
                Toast.makeText(
                    this,
                    getString(R.string.settings_add_product_success),
                    Toast.LENGTH_SHORT
                ).show()
                etName.text = null
                etDescription.text = null
                etPrice.text = null
                etStock.text = null
                selectedImages.clear()
                tvImagesHint.text = getString(R.string.settings_no_images_selected)
            }
        }

        if (selectedImages.isEmpty()) {
            tvImagesHint.text = getString(R.string.settings_no_images_selected)
        }
    }

    private fun onImagesSelected(uris: List<Uri>) {
        val tvImagesHint = findViewById<TextView>(R.id.tvImagesHint)
        selectedImages.clear()
        selectedImages.addAll(uris)

        val text = if (selectedImages.isEmpty()) {
            getString(R.string.settings_no_images_selected)
        } else {
            resources.getQuantityString(
                R.plurals.settings_selected_images,
                selectedImages.size,
                selectedImages.size
            )
        }

        tvImagesHint.text = text
    }
}
