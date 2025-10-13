package com.example.thehub.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thehub.R
import com.example.thehub.data.model.Product

class ProductAdapter(
    private val onClick: (Product) -> Unit = {}
) : ListAdapter<Product, ProductAdapter.ProductVH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(old: Product, new: Product): Boolean =
                old.id == new.id

            override fun areContentsTheSame(old: Product, new: Product): Boolean =
                old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductVH(view, onClick)
    }

    override fun onBindViewHolder(holder: ProductVH, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductVH(
        itemView: View,
        private val onClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvDesc = itemView.findViewById<TextView>(R.id.tvDesc)
        private val tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
        private val ivThumb = itemView.findViewById<ImageView>(R.id.ivThumb)

        fun bind(item: Product) {
            tvName.text = item.name
            tvDesc.text = item.description
            tvPrice.text = "$${String.format("%,.0f", item.price)}"

            // Si aún no tienes URL real, deja un placeholder:
            ivThumb.setImageResource(R.drawable.logo_thehub)

            itemView.setOnClickListener { onClick(item) }
        }
    }
}
