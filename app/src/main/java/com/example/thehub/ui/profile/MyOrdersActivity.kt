package com.example.thehub.ui.profile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thehub.databinding.ActivityMyOrdersBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyOrdersBinding
    private val viewModel: MyOrdersViewModel by viewModels()

    private val adapter = MyOrdersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getIntExtra("USER_ID", 0)

        setupUI()
        setupObservers()

        if (userId != 0) {
            viewModel.loadOrders(userId)
        }
    }

    private fun setupUI() {

        binding.btnBack.setOnClickListener { finish() }

        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.orders.collectLatest { list ->
                adapter.submitList(list)


                binding.tvEmpty.isVisible = list.isEmpty()
                binding.rvOrders.isVisible = list.isNotEmpty()
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.progressBar.isVisible = loading

                if (loading) binding.tvEmpty.isVisible = false
            }
        }
    }
}