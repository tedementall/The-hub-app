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
    // Asegúrate de que tu Adapter reciba la lista o tenga un método submitList
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
        // CORRECCIÓN 1: Cambiamos 'toolbar' por 'btnBack'
        binding.btnBack.setOnClickListener { finish() }

        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.orders.collectLatest { list ->
                adapter.submitList(list)

                // CORRECCIÓN 2: Ahora 'tvEmpty' ya existe en el XML y no dará error
                // Mostramos el mensaje vacío si la lista es 0 y no estamos cargando
                binding.tvEmpty.isVisible = list.isEmpty()
                binding.rvOrders.isVisible = list.isNotEmpty()
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.progressBar.isVisible = loading
                // Ocultamos el mensaje vacío si está cargando para que no parpadee
                if (loading) binding.tvEmpty.isVisible = false
            }
        }
    }
}