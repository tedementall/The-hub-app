package com.example.thehub.ui.home

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thehub.R
import com.example.thehub.databinding.ActivityHomeBinding
import com.example.thehub.di.ServiceLocator
import kotlinx.coroutines.flow.collectLatest

class HomeActivity : ComponentActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val adapter = ProductAdapter()

    private val vm: HomeViewModel by viewModels {
        val repo = ServiceLocator.productRepository
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // animar logo top, opcional
        binding.ivLogoTop.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_scale))

        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter

        lifecycleScope.launchWhenStarted {
            vm.state.collectLatest { st ->
                if (st.error != null) {
                    Toast.makeText(this@HomeActivity, st.error, Toast.LENGTH_SHORT).show()
                }
                adapter.submit(st.products)
            }
        }

        vm.load()
    }
}
