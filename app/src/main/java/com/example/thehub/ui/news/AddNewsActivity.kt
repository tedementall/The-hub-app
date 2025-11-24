package com.example.thehub.ui.blog

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.thehub.data.model.CreateNewsRequest
import com.example.thehub.data.model.ProductImage
import com.example.thehub.databinding.ActivityAddNewsBinding
import com.example.thehub.di.ServiceLocator
import com.example.thehub.utils.ImageUtils
import com.example.thehub.utils.TokenStore
import kotlinx.coroutines.launch

class AddNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewsBinding
    private val newsRepository = ServiceLocator.newsRepository
    private var selectedImageUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivPreview.setImageURI(uri)
            binding.ivPreview.visibility = View.VISIBLE
            binding.layoutPlaceholder.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.cardPickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnPublish.setOnClickListener { validateAndSubmit() }
    }

    private fun validateAndSubmit() {
        val title = binding.etNewsTitle.text.toString().trim()
        val body = binding.etNewsBody.text.toString().trim()

        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(this, "Completa el título y el contenido", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "La imagen de portada es obligatoria", Toast.LENGTH_SHORT).show()
            return
        }

        uploadAndCreateNews(title, body)
    }

    private fun uploadAndCreateNews(title: String, body: String) {
        lifecycleScope.launch {
            setLoading(true)

            try {
                val uri = selectedImageUri ?: return@launch
                val part = ImageUtils.createImagePart(this@AddNewsActivity, uri, "content")
                    ?: run {
                        Toast.makeText(this@AddNewsActivity, "Error al preparar imagen", Toast.LENGTH_SHORT).show()
                        setLoading(false)
                        return@launch
                    }

                var uploadedImage = newsRepository.uploadNewsImage(listOf(part))

                if (uploadedImage == null) {
                    Toast.makeText(this@AddNewsActivity, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                    setLoading(false)
                    return@launch
                }

                if (uploadedImage.url.isNullOrEmpty()) {
                    val baseUrl = "https://x8ki-letl-twmt.n7.xano.io"
                    uploadedImage = uploadedImage.copy(url = "$baseUrl${uploadedImage.path}")
                }

                val request = CreateNewsRequest(
                    title = title,
                    body = body,
                    cover = listOf(uploadedImage)
                )

                val token = TokenStore.read(this@AddNewsActivity) ?: ""
                val success = newsRepository.createNews(token, request)

                if (success) {
                    Toast.makeText(this@AddNewsActivity, "¡Noticia publicada!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@AddNewsActivity, "Error al guardar noticia", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@AddNewsActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnPublish.isEnabled = !isLoading
        binding.cardPickImage.isEnabled = !isLoading
        binding.btnPublish.text = if (isLoading) "Publicando..." else "Publicar Noticia"
    }
}