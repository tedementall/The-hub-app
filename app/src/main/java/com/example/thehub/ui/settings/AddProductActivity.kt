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
import com.example.thehub.data.model.Product
import com.example.thehub.data.model.ProductImage
import com.example.thehub.databinding.ActivityAddProductBinding
import com.example.thehub.di.ServiceLocator
import com.example.thehub.utils.ImageUtils
import com.example.thehub.utils.TokenStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val productRepository = ServiceLocator.productRepository

    private var selectedImageUris: List<Uri> = emptyList()
    private var productToEdit: Product? = null
    private var isEditMode: Boolean = false

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris = uris
            val count = uris.size
            binding.tvImageStatus.text = "$count imágenes nuevas seleccionadas"
            binding.tvImageStatus.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productToEdit = intent.getParcelableExtra("extra_product")
        isEditMode = productToEdit != null

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val categories = listOf("audio", "cargador", "carcasas", "cables", "telefono")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.acCategory.setAdapter(adapter)

        if (isEditMode) {
            setupEditMode()
        } else {
            binding.acCategory.setText(categories[0], false)
        }
    }

    private fun setupEditMode() {
        productToEdit?.let { product ->
            binding.tvTitleScreen.text = "Editar Producto"
            binding.btnAddProduct.text = "Guardar Cambios"
            binding.btnAddProduct.setIconResource(com.example.thehub.R.drawable.ic_edit)
            binding.btnDeleteProduct.visibility = View.VISIBLE

            binding.etName.setText(product.name)
            binding.etLongDesc.setText(product.description)
            binding.etPrice.setText(product.price.toString())
            binding.etStock.setText(product.stockQuantity.toString())
            binding.acCategory.setText(product.category ?: "", false)

            val imgCount = product.imageUrl?.size ?: 0
            if (imgCount > 0) {
                binding.tvImageStatus.text = "Hay $imgCount imágenes guardadas.\nToca para agregar más."
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.cardPickImages.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnAddProduct.setOnClickListener { validateAndSubmit() }

        binding.btnDeleteProduct.setOnClickListener { confirmDelete() }
    }

    private fun confirmDelete() {
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Eliminar producto?")
            .setMessage("Estás a punto de borrar '${productToEdit?.name}'. Esta acción no se puede deshacer.")
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Eliminar") { dialog, _ ->
                deleteProductFlow()
                dialog.dismiss()
            }
            .show()
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

        if (!isEditMode && selectedImageUris.isEmpty()) {
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
                val finalImageList = mutableListOf<ProductImage>()

                if (isEditMode && productToEdit?.imageUrl != null) {
                    finalImageList.addAll(productToEdit!!.imageUrl!!)
                }

                if (selectedImageUris.isNotEmpty()) {
                    val newUploadedImages = uploadImagesRaw()
                    if (newUploadedImages != null) {
                        finalImageList.addAll(newUploadedImages)
                    } else {
                        setLoading(false)
                        return@launch
                    }
                }

                val finalRequest = requestBase.copy(imageUrl = finalImageList)

                if (isEditMode) {
                    val updatedProduct = productRepository.editProduct(token, productToEdit!!.id!!, finalRequest)
                    if (updatedProduct != null) {
                        Toast.makeText(this@AddProductActivity, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddProductActivity, "Error al actualizar el producto", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val createdProduct = productRepository.createProduct(token, finalRequest)
                    if (createdProduct != null) {
                        Toast.makeText(this@AddProductActivity, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddProductActivity, "Error al crear el producto", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@AddProductActivity, "Ocurrió un error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } finally {
                setLoading(false)
            }
        }
    }

    private suspend fun uploadImagesRaw(): List<ProductImage>? {
        val uploadedList = mutableListOf<ProductImage>()
        val baseUrl = "https://x8ki-letl-twmt.n7.xano.io"

        for (uri in selectedImageUris) {
            val imagePart = ImageUtils.prepareFileParts(this, listOf(uri))
            if (imagePart.isNotEmpty()) {
                val response = productRepository.uploadImages(imagePart)
                if (!response.isNullOrEmpty()) {
                    val completedImages = response.map { img ->
                        img.copy(url = if (img.url.isNullOrEmpty()) "$baseUrl${img.path}" else img.url)
                    }
                    uploadedList.addAll(completedImages)
                } else {
                    Toast.makeText(this, "Error al subir una de las imágenes", Toast.LENGTH_SHORT).show()
                    return null
                }
            }
        }
        return uploadedList
    }

    private fun deleteProductFlow() {
        lifecycleScope.launch {
            setLoading(true)
            val token = TokenStore.read(this@AddProductActivity) ?: ""
            val success = productRepository.deleteProduct(token, productToEdit!!.id!!)

            setLoading(false)
            if (success) {
                Toast.makeText(this@AddProductActivity, "Producto eliminado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@AddProductActivity, "No se pudo eliminar el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnAddProduct.isEnabled = !isLoading
        binding.btnDeleteProduct.isEnabled = !isLoading

        if (isLoading) {
            binding.btnAddProduct.text = "Procesando..."
        } else {
            binding.btnAddProduct.text = if (isEditMode) "Guardar Cambios" else "Publicar Producto"
        }
    }
}