package com.example.thehub.ui.blog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Asegúrate de tener esta importación
import com.example.thehub.data.model.News
import com.example.thehub.databinding.ItemBlogBinding

// 1. Agregamos 'onNewsClick' al constructor para manejar el click
class BlogAdapter(
    private var newsList: List<News> = emptyList(),
    private val onNewsClick: (News) -> Unit
) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    fun updateData(newNews: List<News>) {
        newsList = newNews
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = ItemBlogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount() = newsList.size

    // 2. Usamos 'inner class' para poder acceder a 'onNewsClick'
    inner class BlogViewHolder(private val binding: ItemBlogBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            binding.tvBlogTitle.text = news.title
            binding.tvBlogContent.text = news.content

            // 3. Cargar la imagen con Glide
            // (Si tu modelo de imagen es distinto, ajusta news.image?.url)
            news.image?.url?.let { url ->
                Glide.with(binding.root.context)
                    .load(url)
                    .centerCrop()
                    .into(binding.ivBlogImage)
            }

            // 4. Configurar el evento Click en toda la tarjeta
            binding.root.setOnClickListener {
                onNewsClick(news)
            }
        }
    }
}