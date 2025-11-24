package com.example.thehub.ui.profile

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.thehub.R
import com.example.thehub.data.model.Order
import com.example.thehub.databinding.ItemOrderBinding
import java.util.Locale

class MyOrdersAdapter : ListAdapter<Order, MyOrdersAdapter.OrderViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            val displayId = if (order.orderNumber != 0) order.orderNumber else order.id
            binding.tvOrderNumber.text = "Orden #$displayId"

            binding.tvTotal.text = String.format(Locale.US, "$%,.0f", order.totalAmount)

            if (!order.items.isNullOrEmpty()) {
                val firstItem = order.items[0]
                val prod = firstItem.product

                if (prod != null) {
                    binding.tvProductName.text = prod.name

                    if (!prod.imageUrl.isNullOrEmpty()) {
                        binding.ivProductImage.load(prod.imageUrl) {
                            crossfade(true)
                            placeholder(R.drawable.ic_receipt)
                            error(R.drawable.ic_receipt)
                        }
                    } else {
                        binding.ivProductImage.setImageResource(R.drawable.ic_receipt)
                    }
                } else {
                    binding.tvProductName.text = "Items: ${order.items.size}"
                    binding.ivProductImage.setImageResource(R.drawable.ic_receipt)
                }
            } else {
                binding.tvProductName.text = "Procesando..."
                binding.ivProductImage.setImageResource(R.drawable.ic_receipt)
            }

            binding.tvStatus.text = order.status
            setStatusStyle(order.status)
        }

        private fun setStatusStyle(status: String) {
            val (textColor, _) = when (status.lowercase()) {
                "por confirmar" -> "#E65100" to "#FFF3E0"
                "confirmado", "en local" -> "#1565C0" to "#E3F2FD"
                "en transporte", "enviado" -> "#2E7D32" to "#E8F5E9"
                "finalizado" -> "#424242" to "#EEEEEE"
                else -> "#7B1FA2" to "#F3E5F5"
            }
            try {
                binding.tvStatus.setTextColor(Color.parseColor(textColor))
            } catch (e: Exception) { }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Order, newItem: Order) = oldItem == newItem
    }
}