package com.example.thehub.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thehub.R
import com.example.thehub.data.model.Product
import com.example.thehub.utils.asVaultUrl

// 1. Agregamos 'layoutRes' al constructor con un valor por defecto
class ProductAdapter(
    private val layoutRes: Int = R.layout.item_product, // Por defecto usa el antiguo
    private val onClick: (Product) -> Unit = {}
) : ListAdapter<Product, ProductAdapter.ProductVH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(old: Product, new: Product): Boolean = old.id == new.id
            override fun areContentsTheSame(old: Product, new: Product): Boolean = old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
        // 2. Usamos la variable layoutRes en lugar de R.layout.item_product fijo
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ProductVH(view, onClick)
    }

    override fun onBindViewHolder(holder: ProductVH, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductVH(itemView: View, private val onClick: (Product) -> Unit) : RecyclerView.ViewHolder(itemView) {
        // Como usamos los MISMOS IDs (tvName, ivThumb) en ambos XML, esto funciona perfecto
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvDesc = itemView.findViewById<TextView>(R.id.tvDesc) // Puede ser null en el grid, no pasa nada
        private val tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
        private val ivThumb = itemView.findViewById<ImageView>(R.id.ivThumb)

        fun bind(item: Product) {
            tvName.text = item.name
            // Verificamos si tvDesc existe (porque en el Grid lo pusimos gone/oculto)
            tvDesc?.text = item.description
            tvPrice.text = "$${String.format("%,.0f", item.price)}"

            val first = item.imageUrl?.firstOrNull()
            val url = first?.path.asVaultUrl()

            Glide.with(itemView)
                .load(url)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(ivThumb)

            itemView.setOnClickListener { onClick(item) }
        }
    }
}