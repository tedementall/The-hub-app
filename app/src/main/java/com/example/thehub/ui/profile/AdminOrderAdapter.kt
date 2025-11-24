package com.example.thehub.ui.settings

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thehub.data.model.Order
import com.example.thehub.databinding.ItemAdminOrderBinding
import java.text.NumberFormat
import java.util.Locale

class AdminOrderAdapter(
    private var orders: List<Order>,
    private val onStatusClick: (Order) -> Unit,
    private val onDeleteClick: (Order) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: ItemAdminOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemAdminOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.binding.apply {
            tvOrderNumber.text = "Pedido #${order.id}"
            tvOrderStatus.text = order.status


            val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))



            when (order.status) {
                "por confirmar" -> tvOrderStatus.setTextColor(Color.parseColor("#F57C00")) // Naranja
                "finalizado" -> tvOrderStatus.setTextColor(Color.parseColor("#388E3C")) // Verde
                "enviado" -> tvOrderStatus.setTextColor(Color.parseColor("#1976D2")) // Azul
                else -> tvOrderStatus.setTextColor(Color.parseColor("#666666")) // Gris
            }


            btnChangeStatus.setOnClickListener {
                onStatusClick(order)
            }


            btnDeleteOrder.setOnClickListener {
                onDeleteClick(order)
            }
        }
    }

    override fun getItemCount() = orders.size

    fun updateList(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}