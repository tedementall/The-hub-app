package com.example.thehub.ui.settings.addproduct

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.thehub.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddProductActivity : AppCompatActivity() {

    private val selectedImages = mutableListOf<Uri>()

    private val selectImagesLauncher =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            onImagesSelected(uris)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val btnSelectImages = findViewById<MaterialButton>(R.id.btnSelectImages)
        val btnAddProduct = findViewById<MaterialButton>(R.id.btnAddProduct)
        val tvImagesHint = findViewById<TextView>(R.id.tvImagesHint)
        val etName = findViewById<TextInputEditText>(R.id.etProductName)
        val etShortDescription = findViewById<TextInputEditText>(R.id.etProductShortDescription)
        val etDescription = findViewById<TextInputEditText>(R.id.etProductDescription)
        val etPrice = findViewById<TextInputEditText>(R.id.etProductPrice)
        val etPrimaryStock = findViewById<TextInputEditText>(R.id.etProductStockPrimary)
        val etSecondaryStock = findViewById<TextInputEditText>(R.id.etProductStockSecondary)
        val etTertiaryStock = findViewById<TextInputEditText>(R.id.etProductStockTertiary)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (selectedImages.isEmpty()) {
            tvImagesHint.text = getString(R.string.add_product_no_images_message)
        }

        btnSelectImages.setOnClickListener {
            selectImagesLauncher.launch(arrayOf("image/*"))
        }

        btnAddProduct.setOnClickListener {
            val name = etName.text?.toString()?.trim().orEmpty()
            val shortDescription = etShortDescription.text?.toString()?.trim().orEmpty()
            val description = etDescription.text?.toString()?.trim().orEmpty()
            val price = etPrice.text?.toString()?.trim().orEmpty()
            val primaryStock = etPrimaryStock.text?.toString()?.trim().orEmpty()

            etName.error = null
            etShortDescription.error = null
            etDescription.error = null
            etPrice.error = null
            etPrimaryStock.error = null

            var hasError = false

            if (name.isEmpty()) {
                etName.error = getString(R.string.add_product_error_required)
                hasError = true
            }
            if (shortDescription.isEmpty()) {
                etShortDescription.error = getString(R.string.add_product_error_required)
                hasError = true
            }
            if (description.isEmpty()) {
                etDescription.error = getString(R.string.add_product_error_required)
                hasError = true
            }
            if (price.isEmpty()) {
                etPrice.error = getString(R.string.add_product_error_required)
                hasError = true
            }
            if (primaryStock.isEmpty()) {
                etPrimaryStock.error = getString(R.string.add_product_error_required)
                hasError = true
            }

            if (!hasError) {
                Toast.makeText(
                    this,
                    getString(R.string.add_product_success_message),
                    Toast.LENGTH_SHORT
                ).show()

                etName.text = null
                etShortDescription.text = null
                etDescription.text = null
                etPrice.text = null
                etPrimaryStock.text = null
                etSecondaryStock.text = null
                etTertiaryStock.text = null
                selectedImages.clear()
                tvImagesHint.text = getString(R.string.add_product_no_images_message)
            }
        }
    }

    private fun onImagesSelected(uris: List<Uri>) {
        val tvImagesHint = findViewById<TextView>(R.id.tvImagesHint)
        selectedImages.clear()
        selectedImages.addAll(uris)

        val text = if (selectedImages.isEmpty()) {
            getString(R.string.add_product_no_images_message)
        } else {
            resources.getQuantityString(
                R.plurals.add_product_selected_images_count,
                selectedImages.size,
                selectedImages.size
            )
        }

        tvImagesHint.text = text
    }
}
