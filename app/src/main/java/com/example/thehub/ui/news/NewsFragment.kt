package com.example.thehub.ui.news

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thehub.R
import com.example.thehub.databinding.FragmentNewsBinding
import com.example.thehub.di.ServiceLocator
import kotlinx.coroutines.launch

class NewsFragment : Fragment(R.layout.fragment_news) {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NewsAdapter
    private val newsRepository = ServiceLocator.newsRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewsBinding.bind(view)

        setupRecyclerView()
        loadNews()
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter(emptyList()) { news ->

            val bottomSheet = NewsDetailBottomSheet.newInstance(news)
            bottomSheet.show(parentFragmentManager, "NewsDetailBottomSheet")
        }

        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@NewsFragment.adapter
        }
    }

    private fun loadNews() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {

                binding.progressBar.visibility = View.VISIBLE
                binding.rvNews.visibility = View.GONE
                binding.tvError.visibility = View.GONE


                val newsList = newsRepository.getNews()

                if (newsList.isEmpty()) {

                    binding.progressBar.visibility = View.GONE
                    binding.tvError.text = "No hay noticias disponibles"
                    binding.tvError.visibility = View.VISIBLE
                } else {

                    adapter.updateList(newsList)
                    binding.progressBar.visibility = View.GONE
                    binding.rvNews.visibility = View.VISIBLE
                }

            } catch (e: Exception) {

                e.printStackTrace()
                binding.progressBar.visibility = View.GONE
                binding.tvError.text = "Error al cargar noticias: ${e.message}"
                binding.tvError.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}