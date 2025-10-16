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

    // imágenes seleccionadas
    private var pickedUris: List<Uri> = emptyList()

    // Picker múltiple (galería)
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

        // Seleccionar imágenes
        b.cardPickImages.setOnClickListener { pickImages.launch("image/*") }

        // Crear producto
        b.btnAddProduct.setOnClickListener { onAddProductClick() }
    }

    private fun onAddProductClick() {
        // 1) leer inputs
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

        // 2) validaciones mínimas
        if (name.isBlank() || description.isBlank() || price == null || price <= 0.0 || stock == null || stock < 0) {
            Toast.makeText(this, "Completa nombre, descripción, precio (>0) y stock (>=0).", Toast.LENGTH_LONG).show()
            return
        }

        setLoading(true)

        // 3) corrutina: crear → (opcional) subir → patch
        lifecycleScope.launch {
            try {
                val repo = ServiceLocator.productRepository

                // 3.1) POST /product
                val body = CreateProductRequest(
                    name = name,
                    description = description,
                    price = price,
                    stockQuantity = stock,     // << si tu modelo se llama distinto, ajusta el nombre
                    imageUrl = null            // imágenes se agregan con PATCH
                )
                val created: Product = repo.createProduct(body)

                // 3.2) si no hay imágenes, terminamos aquí
                if (pickedUris.isEmpty()) {
                    toastSuccess("Producto creado id=${created.id}")
                    finish()
                    return@launch
                }

                // 3.3) subir imágenes a /upload/image
                val uploaded: List<ProductImage> = uploadImages(pickedUris)

                // 3.4) patch product con image_url
                val updated = repo.patchImages(
                    id = created.id!!,
                    body = PatchImagesRequest(imageUrl = uploaded) // << ajusta si tu data class usa otro nombre
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

    /** Construye las partes multipart "content[]" y llama al UploadService */
    private suspend fun uploadImages(uris: List<Uri>): List<ProductImage> {
        val uploadService = ServiceLocator.uploadService
        val parts = withContext(Dispatchers.IO) { buildImageParts(uris) }
        return uploadService.uploadImages(parts)
    }

    /** Crea los MultipartBody.Part con nombre EXACTO "content[]" */
    private fun buildImageParts(uris: List<Uri>): List<MultipartBody.Part> {
        val cr = contentResolver
        val parts = mutableListOf<MultipartBody.Part>()

        for (uri in uris) {
            val mime = cr.getType(uri) ?: "image/jpeg"
            val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "jpg"
            val fileName = "img_${System.currentTimeMillis()}.$ext"

            val bytes = cr.openInputStream(uri)?.use { it.readBytes() } ?: continue
            val reqBody: RequestBody = RequestBody.create(mime.toMediaTypeOrNull(), bytes)

            // CLAVE para Xano:
            // el nombre del campo debe ser "content[]"
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

    private fun errorMessage(e: Throwable): String {
        return when (e) {
            is HttpException -> {
                val body = e.response()?.errorBody()?.string()
                "HTTP ${e.code()} ${e.message()} ${body ?: ""}".trim()
            }
            else -> e.message ?: e.toString()
        }
    }

    private fun toastSuccess(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
