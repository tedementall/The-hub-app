package com.example.thehub.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.thehub.R
import com.example.thehub.data.model.Product
import com.example.thehub.databinding.BottomSheetProductDetailBinding
import com.example.thehub.utils.asVaultUrl
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.wasabeef.blurry.Blurry

/**
 * ProductDetailBottomSheet con:
 * - Galería de imágenes (ViewPager2)
 * - Blur real usando Blurry
 */
class ProductDetailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetProductDetailBinding? = null
    private val binding get() = _binding!!

    private var product: Product? = null
    private var onAddToCart: ((Product, Int) -> Unit)? = null

    private val imageAdapter = ProductImageAdapter()
    private val dots = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheetBehavior()
        product?.let { setupProductDetails(it) }
    }

    override fun onStart() {
        super.onStart()
        // Aplicar blur al mostrar el dialog
        applyBlur()
    }

    override fun onStop() {
        // Remover blur al cerrar el dialog
        removeBlur()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBottomSheetBehavior() {
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialogInterface as? com.google.android.material.bottomsheet.BottomSheetDialog)
                ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }

            // Reducir dim porque Blurry ya agrega overlay
            dialog?.window?.setDimAmount(0.2f)
        }
    }

    /**
     * Aplica blur gaussiano real al fondo usando Blurry
     */
    private fun applyBlur() {
        try {
            activity?.window?.decorView?.let { decorView ->
                val contentView = decorView.findViewById<ViewGroup>(android.R.id.content)

                Blurry.with(requireContext())
                    .radius(25)                    // Radio del blur (1-25, más = más blur)
                    .sampling(2)                   // Sampling para performance (1-8, más = más rápido)
                    .color(0x33FFFFFF.toInt())     // Overlay oscuro semi-transparente
                    .async()                       // Procesamiento en background thread
                    .animate(200)                  // Animación suave de 200ms
                    .onto(contentView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Si falla, aumentar dim como fallback
            dialog?.window?.setDimAmount(0.75f)
        }
    }

    /**
     * Remueve el blur del fondo
     */
    private fun removeBlur() {
        try {
            activity?.window?.decorView?.let { decorView ->
                val contentView = decorView.findViewById<ViewGroup>(android.R.id.content)
                Blurry.delete(contentView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupProductDetails(product: Product) {
        binding.apply {
            // Configurar galería de imágenes
            setupImageGallery(product)

            // Configurar textos
            tvProductName.text = product.name
            tvProductDescription.text = product.description
            tvProductPrice.text = "$${String.format("%,.0f", product.price)}"
            tvStock.text = "Stock disponible: ${product.stockQuantity} unidades"

            // Estado del stock
            val stockColor = when {
                product.stockQuantity == 0 -> ContextCompat.getColor(requireContext(), R.color.stock_out)
                product.stockQuantity < 5 -> ContextCompat.getColor(requireContext(), R.color.stock_low)
                else -> ContextCompat.getColor(requireContext(), R.color.stock_available)
            }
            tvStock.setTextColor(stockColor)

            // Deshabilitar botón si no hay stock
            btnAddToCart.isEnabled = product.stockQuantity > 0

            if (product.stockQuantity == 0) {
                btnAddToCart.text = "Sin stock"
                btnAddToCart.alpha = 0.5f
            }

            // Configurar contador de cantidad
            var quantity = 1
            tvQuantity.text = quantity.toString()

            btnDecrease.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    tvQuantity.text = quantity.toString()
                }
            }

            btnIncrease.setOnClickListener {
                if (quantity < product.stockQuantity) {
                    quantity++
                    tvQuantity.text = quantity.toString()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Stock máximo alcanzado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Agregar al carrito
            btnAddToCart.setOnClickListener {
                onAddToCart?.invoke(product, quantity)
                Toast.makeText(
                    requireContext(),
                    "Agregado al carrito: ${product.name} (x$quantity)",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }

            // Cerrar
            ivClose.setOnClickListener {
                dismiss()
            }
        }
    }

    /**
     * Configura la galería de imágenes con ViewPager2
     */
    private fun setupImageGallery(product: Product) {
        binding.apply {
            val images = product.imageUrl ?: emptyList()

            if (images.isEmpty()) {
                // Si no hay imágenes, mostrar placeholder
                setupPlaceholderImage()
                return
            }

            // Configurar ViewPager2
            viewPagerImages.adapter = imageAdapter
            imageAdapter.submitList(images)

            // Mostrar/ocultar indicadores según cantidad de imágenes
            if (images.size > 1) {
                // Setup dots indicator
                setupDotsIndicator(images.size)

                // Setup contador de imágenes (1/3, 2/3, etc.)
                updateImageCounter(0, images.size)
                tvImageCounter.isVisible = true

                // Listener para cambios de página
                viewPagerImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        updateDotsIndicator(position)
                        updateImageCounter(position, images.size)
                    }
                })
            } else {
                // Solo una imagen, ocultar indicadores
                dotsIndicator.isVisible = false
                tvImageCounter.isVisible = false
            }
        }
    }

    /**
     * Configura el placeholder cuando no hay imágenes
     */
    private fun setupPlaceholderImage() {
        binding.apply {
            // Ocultar ViewPager y mostrar ImageView simple con placeholder
            viewPagerImages.isVisible = false
            dotsIndicator.isVisible = false
            tvImageCounter.isVisible = false
        }
    }

    /**
     * Configura los puntos indicadores
     */
    private fun setupDotsIndicator(count: Int) {
        binding.dotsIndicator.removeAllViews()
        dots.clear()

        for (i in 0 until count) {
            val dot = ImageView(requireContext()).apply {
                setImageResource(if (i == 0) R.drawable.dot_active else R.drawable.dot_inactive)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    if (i > 0) marginStart = 8.dpToPx()
                }
                layoutParams = params
            }
            dots.add(dot)
            binding.dotsIndicator.addView(dot)
        }
    }

    /**
     * Actualiza el indicador de dots según la página actual
     */
    private fun updateDotsIndicator(position: Int) {
        dots.forEachIndexed { index, dot ->
            dot.setImageResource(
                if (index == position) R.drawable.dot_active
                else R.drawable.dot_inactive
            )
        }
    }

    /**
     * Actualiza el contador de imágenes (1/3, 2/3, etc.)
     */
    private fun updateImageCounter(position: Int, total: Int) {
        binding.tvImageCounter.text = "${position + 1}/$total"
    }

    /**
     * Extension function para convertir dp a px
     */
    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    companion object {
        fun newInstance(
            product: Product,
            onAddToCart: (Product, Int) -> Unit
        ): ProductDetailBottomSheet {
            return ProductDetailBottomSheet().apply {
                this.product = product
                this.onAddToCart = onAddToCart
            }
        }
    }
}