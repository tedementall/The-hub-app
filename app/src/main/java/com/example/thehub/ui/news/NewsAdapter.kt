package com.example.thehub.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.thehub.R
import com.example.thehub.data.model.News
import com.example.thehub.databinding.ItemNewsBinding

class NewsAdapter(
    private var newsList: List<News>,
    private val onNewsClick: (News) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(news: News) {
            binding.tvNewsTitle.text = news.title
            binding.tvNewsContent.text = news.body


            val imageUrl = news.cover?.firstOrNull()?.url

            if (!imageUrl.isNullOrEmpty()) {
                binding.ivNewsImage.load(imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                }
            } else {
                binding.ivNewsImage.setImageResource(R.drawable.ic_placeholder)
            }

            binding.root.setOnClickListener { onNewsClick(news) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount(): Int = newsList.size

    fun updateList(newList: List<News>) {
        newsList = newList
        notifyDataSetChanged()
    }
}