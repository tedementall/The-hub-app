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
import com.example.thehub.R // <--- ESTA LÍNEA ES CRUCIAL PARA SOLUCIONAR EL ERROR DE 'R'
import com.example.thehub.data.model.Product
import com.example.thehub.databinding.BottomSheetProductDetailBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.wasabeef.blurry.Blurry

class ProductDetailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetProductDetailBinding? = null
    private val binding get() = _binding!!

    private var product: Product? = null
    private var onAddToCart: ((Product, Int) -> Unit)? = null

    // Asegúrate de tener esta clase ProductImageAdapter creada en tu proyecto
    private val imageAdapter = ProductImageAdapter()
    private val dots = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asegúrate de que este estilo exista en themes.xml
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
        applyBlur()
    }

    override fun onStop() {
        removeBlur()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBottomSheetBehavior() {
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as? BottomSheetDialog
            val bottomSheet = bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
            dialog?.window?.setDimAmount(0.2f)
        }
    }

    private fun applyBlur() {
        try {
            activity?.window?.decorView?.let { decorView ->
                val contentView = decorView.findViewById<ViewGroup>(android.R.id.content)

                Blurry.with(requireContext())
                    .radius(25)
                    .sampling(2)
                    .color(0x33FFFFFF.toInt())
                    .async()
                    .animate(200)
                    .onto(contentView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            dialog?.window?.setDimAmount(0.75f)
        }
    }

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
            setupImageGallery(product)

            tvProductName.text = product.name
            tvProductDescription.text = product.description
            tvProductPrice.text = "$${String.format("%,.0f", product.price)}"
            tvStock.text = "Stock disponible: ${product.stockQuantity} unidades"

            // Estos colores deben existir en colors.xml
            val stockColor = when {
                product.stockQuantity == 0 -> ContextCompat.getColor(requireContext(), R.color.stock_out)
                product.stockQuantity < 5 -> ContextCompat.getColor(requireContext(), R.color.stock_low)
                else -> ContextCompat.getColor(requireContext(), R.color.stock_available)
            }
            tvStock.setTextColor(stockColor)

            btnAddToCart.isEnabled = product.stockQuantity > 0

            if (product.stockQuantity == 0) {
                btnAddToCart.text = "Sin stock"
                btnAddToCart.alpha = 0.5f
            }

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
                    Toast.makeText(requireContext(), "Stock máximo alcanzado", Toast.LENGTH_SHORT).show()
                }
            }

            btnAddToCart.setOnClickListener {
                onAddToCart?.invoke(product, quantity)
                dismiss()
            }

            ivClose.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setupImageGallery(product: Product) {
        binding.apply {
            val images = product.imageUrl ?: emptyList()

            if (images.isEmpty()) {
                setupPlaceholderImage()
                return
            }

            viewPagerImages.adapter = imageAdapter
            imageAdapter.submitList(images)

            if (images.size > 1) {
                setupDotsIndicator(images.size)
                updateImageCounter(0, images.size)
                tvImageCounter.isVisible = true

                viewPagerImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        updateDotsIndicator(position)
                        updateImageCounter(position, images.size)
                    }
                })
            } else {
                dotsIndicator.isVisible = false
                tvImageCounter.isVisible = false
            }
        }
    }

    private fun setupPlaceholderImage() {
        binding.apply {
            viewPagerImages.isVisible = false
            dotsIndicator.isVisible = false
            tvImageCounter.isVisible = false
        }
    }

    private fun setupDotsIndicator(count: Int) {
        binding.dotsIndicator.removeAllViews()
        dots.clear()

        for (i in 0 until count) {
            val dot = ImageView(requireContext()).apply {
                // Estos drawables deben existir
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

    private fun updateDotsIndicator(position: Int) {
        dots.forEachIndexed { index, dot ->
            dot.setImageResource(
                if (index == position) R.drawable.dot_active
                else R.drawable.dot_inactive
            )
        }
    }

    private fun updateImageCounter(position: Int, total: Int) {
        binding.tvImageCounter.text = "${position + 1}/$total"
    }

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