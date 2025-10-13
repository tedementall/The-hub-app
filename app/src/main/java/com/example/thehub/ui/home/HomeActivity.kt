package com.example.thehub.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thehub.databinding.ActivityHomeBinding
import com.example.thehub.di.ServiceLocator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeActivity : ComponentActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val adapter = ProductAdapter()

    // VM manual (sin Hilt):
    private val vm by viewModels<HomeViewModel> {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(ServiceLocator.productRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        vm.state.onEach { st ->
            binding.progressBar.visibility = if (st.loading) android.view.View.VISIBLE else android.view.View.GONE
            st.error?.let { /* show a toast/snackbar */ }
            adapter.submit(st.products)
        }.launchIn(lifecycleScope)

        vm.load()
    }
}
