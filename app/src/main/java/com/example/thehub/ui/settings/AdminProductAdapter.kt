package com.example.thehub.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Asegúrate de tener Glide o usa tu ImageUtils
import com.example.thehub.data.model.Product
import com.example.thehub.databinding.ItemAdminProductBinding

class AdminProductAdapter(
    private var products: List<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemAdminProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemAdminProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        with(holder.binding) {
            tvName.text = product.name
            tvPrice.text = "$ ${product.price}"
            chipStock.text = "Stock: ${product.stockQuantity}"

            // Cargar imagen (Asumiendo que tienes Glide, si no usa tu ImageUtils)
            val imgUrl = product.imageUrl?.firstOrNull()?.url
            if (!imgUrl.isNullOrEmpty()) {
                Glide.with(root.context).load(imgUrl).into(ivProduct)
            }

            btnEdit.setOnClickListener { onEditClick(product) }
            btnDelete.setOnClickListener { onDeleteClick(product) }
        }
    }

    override fun getItemCount() = products.size

    fun updateList(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}