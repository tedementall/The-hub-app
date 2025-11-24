package com.example.thehub.ui.settings

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.thehub.data.model.CreateProductRequest
import com.example.thehub.databinding.ActivityAddProductBinding
import com.example.thehub.di.ServiceLocator
import com.example.thehub.utils.ImageUtils
import com.example.thehub.utils.TokenStore
import kotlinx.coroutines.launch

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val productRepository = ServiceLocator.productRepository

    private var selectedImageUris: List<Uri> = emptyList()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris = uris
            val count = uris.size
            binding.tvImageStatus.text = "$count imágenes seleccionadas ✔️"
            binding.tvImageStatus.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDropdown()
        setupListeners()
    }

    private fun setupDropdown() {
        val categories = listOf("audio", "cargador", "carcasas", "cables", "telefono")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.acCategory.setAdapter(adapter)
        binding.acCategory.setText(categories[0], false)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.cardPickImages.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnAddProduct.setOnClickListener { validateAndSubmit() }
    }

    private fun validateAndSubmit() {
        val name = binding.etName.text.toString().trim()
        val category = binding.acCategory.text.toString()
        val description = binding.etLongDesc.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val stockStr = binding.etStock.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Debes seleccionar al menos una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0
        val stock = stockStr.toIntOrNull() ?: 0

        if (price <= 0.0) {
            Toast.makeText(this, "El precio debe ser mayor a 0", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBase = CreateProductRequest(
            name = name,
            description = description,
            price = price,
            stockQuantity = stock,
            category = category,
            imageUrl = null
        )

        uploadProductFlow(requestBase)
    }

    private fun uploadProductFlow(requestBase: CreateProductRequest) {
        lifecycleScope.launch {
            setLoading(true)
            try {
                val token = TokenStore.read(this@AddProductActivity) ?: ""

                // 1. Subir cada imagen individualmente
                if (selectedImageUris.isNotEmpty()) {
                    val allUploadedImages = mutableListOf<com.example.thehub.data.model.ProductImage>()

                    // Loop para subir cada imagen una por una
                    for (uri in selectedImageUris) {
                        val imagePart = ImageUtils.prepareFileParts(this@AddProductActivity, listOf(uri))

                        if (imagePart.isNotEmpty()) {
                            val uploadedImages = productRepository.uploadImages(imagePart)

                            if (uploadedImages != null && uploadedImages.isNotEmpty()) {
                                allUploadedImages.addAll(uploadedImages)
                            } else {
                                Toast.makeText(this@AddProductActivity, "Error al subir imagen ${allUploadedImages.size + 1}", Toast.LENGTH_SHORT).show()
                                setLoading(false)
                                return@launch
                            }
                        }
                    }

                    if (allUploadedImages.isNotEmpty()) {
                        // 2. Agregar el campo URL a cada imagen
                        val baseUrl = "https://x8ki-letl-twmt.n7.xano.io"
                        val imagesWithUrl = allUploadedImages.map { image ->
                            image.copy(url = "$baseUrl${image.path}")
                        }

                        // 3. Crear producto con todas las imágenes
                        val finalRequest = requestBase.copy(imageUrl = imagesWithUrl)
                        val createdProduct = productRepository.createProduct(token, finalRequest)

                        if (createdProduct != null) {
                            Toast.makeText(this@AddProductActivity, "¡Producto con ${allUploadedImages.size} imágenes publicado con éxito!", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@AddProductActivity, "Error al crear el producto", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AddProductActivity, "Error al subir imágenes", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@AddProductActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnAddProduct.isEnabled = !isLoading
        binding.btnAddProduct.text = if (isLoading) "Procesando..." else "Publicar Producto"
    }
}