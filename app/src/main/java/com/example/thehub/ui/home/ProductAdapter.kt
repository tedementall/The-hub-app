package com.example.thehub.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.thehub.R
import com.example.thehub.data.model.Product

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.VH>() {

    private val items = mutableListOf<Product>()

    fun submit(list: List<Product>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val img: ImageView = v.findViewById(R.id.img)
        private val name: TextView = v.findViewById(R.id.name)
        private val desc: TextView = v.findViewById(R.id.desc)
        private val price: TextView = v.findViewById(R.id.price)
        private val btnAdd: TextView = v.findViewById(R.id.btnAdd)

        fun bind(p: Product) {
            name.text = p.name
            desc.text = p.description
            price.text = "$${String.format("%.2f", p.price)}"

            // usa el helper de tu modelo: imageUrl = image?.url ?: image?.path
            img.load(p.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_card)
                error(R.drawable.bg_card)
            }

            // btnAdd.setOnClickListener { /* TODO: más adelante */ }
        }
    }
}
