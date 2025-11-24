package com.example.thehub.ui.blog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thehub.data.model.News
import com.example.thehub.databinding.ItemBlogBinding

class BlogAdapter(private var newsList: List<News> = emptyList()) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

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

    class BlogViewHolder(private val binding: ItemBlogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(news: News) {
            binding.tvBlogTitle.text = news.title
            binding.tvBlogContent.text = news.content


        }
    }
}