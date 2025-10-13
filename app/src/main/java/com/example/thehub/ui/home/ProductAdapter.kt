package com.example.thehub.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thehub.data.model.Product
import com.example.thehub.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val items: MutableList<Product> = mutableListOf()
) : RecyclerView.Adapter<ProductAdapter.VH>() {

    fun submit(list: List<Product>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvName.text = item.name
            tvDesc.text = item.description
            tvPrice.text = formatPrice(item.price)
            // Si quieres, carga imagen con Coil cuando exista URL real
            // ivThumb.load(item.image?.path) { crossfade(true) }
        }
    }

    override fun getItemCount(): Int = items.size

    private fun formatPrice(value: Double): String {
        val nf = NumberFormat.getCurrencyInstance(Locale("es", "CL")) // ajusta si quieres otra moneda
        return nf.format(value)
    }
}
