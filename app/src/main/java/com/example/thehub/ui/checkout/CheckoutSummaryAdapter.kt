package com.example.thehub.ui.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thehub.R
import com.example.thehub.data.model.CartProduct
import com.example.thehub.databinding.ItemCheckoutProductBinding
import com.example.thehub.utils.asVaultUrl
import java.util.Locale

class CheckoutSummaryAdapter : ListAdapter<CartProduct, CheckoutSummaryAdapter.CheckoutViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val binding = ItemCheckoutProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CheckoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CheckoutViewHolder(private val binding: ItemCheckoutProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartProduct: CartProduct) {
            val product = cartProduct.product
            val context = itemView.context

            binding.tvProductName.text = product.name
            binding.tvQuantity.text = "Cantidad: ${cartProduct.quantity}"

            val itemTotal = product.price * cartProduct.quantity
            binding.tvItemTotal.text = String.format(Locale.US, "$%,.0f", itemTotal)

            val imageUrl = product.imageUrl?.firstOrNull()?.path.asVaultUrl()
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.ivProductImage)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }
}