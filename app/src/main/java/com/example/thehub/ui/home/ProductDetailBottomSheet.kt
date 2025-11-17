package com.example.thehub.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.thehub.R
import com.example.thehub.data.model.Product
import com.example.thehub.databinding.BottomSheetProductDetailBinding
import com.example.thehub.utils.asVaultUrl
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProductDetailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetProductDetailBinding? = null
    private val binding get() = _binding!!

    private var product: Product? = null
    private var onAddToCart: ((Product, Int) -> Unit)? = null

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

        product?.let { setupProductDetails(it) }
    }

    private fun setupProductDetails(product: Product) {
        binding.apply {
            // Configurar imagen
            val imageUrl = product.imageUrl?.firstOrNull()?.path.asVaultUrl()
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .centerCrop()
                .into(ivProductImage)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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