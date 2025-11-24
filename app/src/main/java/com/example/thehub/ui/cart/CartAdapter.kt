package com.example.thehub.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thehub.R
import com.example.thehub.data.model.CartProduct
import com.example.thehub.databinding.ItemCartProductBinding
import com.example.thehub.utils.asVaultUrl
import java.util.Locale

class CartAdapter(
    private val onQuantityChange: (productId: Int, newQuantity: Int) -> Unit,
    private val onRemoveItem: (productId: Int) -> Unit
) : ListAdapter<CartProduct, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding, onQuantityChange, onRemoveItem)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CartViewHolder(
        private val binding: ItemCartProductBinding,
        private val onQuantityChange: (productId: Int, newQuantity: Int) -> Unit,
        private val onRemoveItem: (productId: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartProduct: CartProduct) {
            val product = cartProduct.product


            binding.tvName.text = product.name


            binding.tvQuantity.text = cartProduct.quantity.toString()


            val priceFormatted = String.format(Locale.US, "%,.0f", product.price)
            binding.tvPrice.text = "$$priceFormatted"


            val imageUrl = product.imageUrl?.firstOrNull()?.path.asVaultUrl()
            Glide.with(itemView)
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder) // Asegúrate de tener este icono o cámbialo
                .into(binding.ivThumb)




            binding.btnPlus.setOnClickListener {
                onQuantityChange(product.id!!, cartProduct.quantity + 1)
            }


            binding.btnMinus.setOnClickListener {
                val currentQty = cartProduct.quantity
                if (currentQty > 1) {

                    onQuantityChange(product.id!!, currentQty - 1)
                } else {

                    onRemoveItem(product.id!!)
                }
            }


        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }
}