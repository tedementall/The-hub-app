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
import java.util.Locale

class ProductAdapter(
    private val layoutRes: Int = R.layout.item_product,
    private val onClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductVH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(old: Product, new: Product): Boolean = old.id == new.id
            override fun areContentsTheSame(old: Product, new: Product): Boolean = old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ProductVH(view, onClick)
    }

    override fun onBindViewHolder(holder: ProductVH, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductVH(itemView: View, private val onClick: (Product) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView? = itemView.findViewById(R.id.tvName)
        private val tvDesc: TextView? = itemView.findViewById(R.id.tvDesc)
        private val tvPrice: TextView? = itemView.findViewById(R.id.tvPrice)
        private val ivThumb: ImageView? = itemView.findViewById(R.id.ivThumb)

        fun bind(item: Product) {
            tvName?.text = item.name
            tvDesc?.text = item.description ?: ""
            tvPrice?.text = String.format(Locale.US, "$%,.0f", item.price)

            val url = item.imageUrl?.firstOrNull()?.path.asVaultUrl()

            if (ivThumb != null) {
                Glide.with(itemView.context)
                    .load(url)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(ivThumb)
            }

            itemView.setOnClickListener { onClick(item) }
        }
    }
}