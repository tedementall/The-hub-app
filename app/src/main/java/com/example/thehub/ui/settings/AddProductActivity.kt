package com.example.thehub.ui.settings

import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.thehub.data.model.CreateProductRequest
import com.example.thehub.data.model.PatchImagesRequest
import com.example.thehub.data.model.Product
import com.example.thehub.data.model.ProductImage
import com.example.thehub.databinding.ActivityAddProductBinding
import com.example.thehub.di.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class AddProductActivity : AppCompatActivity() {

    private lateinit var b: ActivityAddProductBinding

    private var pickedUris: List<Uri> = emptyList()

    private val pickImages = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        pickedUris = uris ?: emptyList()
        b.tvImageStatus.text = when (pickedUris.size) {
            0 -> "Sin imágenes"
            1 -> "1 imagen seleccionada"
            else -> "${pickedUris.size} imágenes seleccionadas"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.cardPickImages.setOnClickListener { pickImages.launch("image/*") }
        b.btnAddProduct.setOnClickListener { onAddProductClick() }
    }

    private fun onAddProductClick() {
        val name = b.etName.text?.toString()?.trim().orEmpty()
        val shortDesc = b.etShortDesc.text?.toString()?.trim().orEmpty()
        val longDesc = b.etLongDesc.text?.toString()?.trim().orEmpty()
        val description = listOf(shortDesc, longDesc)
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .ifBlank { longDesc.ifBlank { shortDesc } }

        val price = b.etPrice.text?.toString()
            ?.replace(",", ".")
            ?.toDoubleOrNull()

        val stock = b.etStock.text?.toString()?.toIntOrNull()

        if (name.isBlank() || description.isBlank() || price == null || price <= 0.0 || stock == null || stock < 0) {
            Toast.makeText(this, "Completa nombre, descripción, precio (>0) y stock (>=0).", Toast.LENGTH_LONG).show()
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val repo = ServiceLocator.productRepository

                val created: Product = repo.createProduct(
                    CreateProductRequest(
                        name = name,
                        description = description,
                        price = price,
                        stockQuantity = stock,
                        imageUrl = null
                    )
                )

                if (pickedUris.isEmpty()) {
                    toastSuccess("Producto creado id=${created.id}")
                    finish()
                    return@launch
                }

                val uploaded: List<ProductImage> = uploadImages(pickedUris)
                if (uploaded.isEmpty()) {
                    Toast.makeText(
                        this@AddProductActivity,
                        "No se pudieron subir las imágenes",
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }

                val updated = repo.patchImages(
                    id = created.id!!,
                    body = PatchImagesRequest(imageUrl = uploaded)
                )

                toastSuccess("Creado id=${updated.id} (imgs=${updated.imageUrl?.size ?: 0})")
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@AddProductActivity, errorMessage(e), Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private suspend fun uploadImages(uris: List<Uri>): List<ProductImage> {
        val uploadService = ServiceLocator.uploadService
        val parts = withContext(Dispatchers.IO) { buildImageParts(uris) }
        if (parts.isEmpty()) return emptyList()
        return uploadService.uploadImages(parts)
    }

    private fun buildImageParts(uris: List<Uri>): List<MultipartBody.Part> {
        val cr = contentResolver
        val parts = mutableListOf<MultipartBody.Part>()

        for (uri in uris) {
            val mime = cr.getType(uri) ?: "image/jpeg"
            val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "jpg"
            val fileName = "img_${System.currentTimeMillis()}.$ext"
            val bytes = cr.openInputStream(uri)?.use { it.readBytes() } ?: continue
            val reqBody: RequestBody = RequestBody.create(mime.toMediaTypeOrNull(), bytes)
            val part = MultipartBody.Part.createFormData("content[]", fileName, reqBody)
            parts += part
        }
        return parts
    }

    private fun setLoading(loading: Boolean) {
        b.btnAddProduct.isEnabled = !loading
        b.cardPickImages.isEnabled = !loading
        b.progressBar.isVisible = loading
        b.btnAddProduct.text = if (loading) "Creando..." else "Añadir Producto"
    }

    private fun errorMessage(e: Throwable): String =
        if (e is HttpException) {
            val body = e.response()?.errorBody()?.string()
            "HTTP ${e.code()} ${e.message()} ${body ?: ""}".trim()
        } else e.message ?: e.toString()

    private fun toastSuccess(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
