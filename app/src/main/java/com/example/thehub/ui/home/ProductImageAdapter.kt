package com.example.thehub.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thehub.R
import com.example.thehub.data.model.ProductImage
import com.example.thehub.utils.asVaultUrl

class ProductImageAdapter : ListAdapter<ProductImage, ProductImageAdapter.ImageViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ProductImage>() {
            override fun areItemsTheSame(old: ProductImage, new: ProductImage): Boolean =
                old.path == new.path

            override fun areContentsTheSame(old: ProductImage, new: ProductImage): Boolean =
                old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<ImageView>(R.id.ivProductImage)

        fun bind(productImage: ProductImage) {
            val imageUrl = productImage.path.asVaultUrl()

            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .centerCrop()
                .into(imageView)
        }
    }
}