package com.example.thehub.ui.blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thehub.databinding.FragmentBlogBinding
import com.example.thehub.di.ServiceLocator
import kotlinx.coroutines.launch

class BlogFragment : Fragment() {

    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { ServiceLocator.blogRepository }
    private val adapter = BlogAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainContainer) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top)
            insets
        }

        binding.rvBlog.layoutManager = LinearLayoutManager(context)
        binding.rvBlog.adapter = adapter

        loadNews()
    }

    private fun loadNews() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvBlog.visibility = View.GONE
        binding.tvError.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val newsList = repository.getNews()

                if (newsList.isNotEmpty()) {
                    adapter.updateData(newsList)
                    binding.rvBlog.visibility = View.VISIBLE
                } else {
                    binding.tvError.text = "No hay noticias disponibles."
                    binding.tvError.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvError.text = "Error de conexión."
                binding.tvError.visibility = View.VISIBLE
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}