package com.example.thehub.ui.settings

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thehub.data.model.Order
import com.example.thehub.data.remote.RetrofitClient
import com.example.thehub.databinding.ActivityAdminOrderListBinding
import kotlinx.coroutines.launch

class AdminOrderListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminOrderListBinding
    private lateinit var adapter: AdminOrderAdapter
    private val api by lazy { RetrofitClient.store() }

    private var allOrders: List<Order> = emptyList()
    private var currentSearchQuery: String = ""
    private var currentStatusFilter: String = "Todos"

    private val availableStatuses = arrayOf(
        "por confirmar", "confirmado", "en local",
        "en transporte", "enviado", "finalizado"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadOrders()
    }

    private fun setupUI() {
        binding.tvTitle.text = "Gestión Pedidos"
        binding.btnBack.setOnClickListener { finish() }


        adapter = AdminOrderAdapter(
            orders = emptyList(),
            onStatusClick = { order -> showStatusDialog(order) },
            onDeleteClick = { order -> showDeleteConfirmation(order) }
        )

        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = adapter

        setupSearch()
        setupFilters()
    }


    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString()
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFilters() {
        binding.apply {
            chipAll.setOnClickListener { updateFilterVisuals(chipAll, "Todos") }
            chipConfirm.setOnClickListener { updateFilterVisuals(chipConfirm, "por confirmar") }
            chipSent.setOnClickListener { updateFilterVisuals(chipSent, "enviado") }
            chipFinal.setOnClickListener { updateFilterVisuals(chipFinal, "finalizado") }
        }
    }

    private fun updateFilterVisuals(selectedChip: TextView, status: String) {
        currentStatusFilter = status
        val allChips = listOf(binding.chipAll, binding.chipConfirm, binding.chipSent, binding.chipFinal)
        allChips.forEach { chip ->
            if (chip == selectedChip) {
                chip.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF9800"))
                chip.setTextColor(Color.WHITE)
            } else {
                chip.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#80FFFFFF"))
                chip.setTextColor(Color.parseColor("#1A1A1A"))
            }
        }
        applyFilters()
    }

    private fun applyFilters() {
        val filteredList = allOrders.filter { order ->
            val matchesSearch = order.id.toString().contains(currentSearchQuery)
            val matchesStatus = if (currentStatusFilter == "Todos") true else order.status.equals(currentStatusFilter, ignoreCase = true)
            matchesSearch && matchesStatus
        }
        adapter.updateList(filteredList)
        binding.tvEmpty.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun loadOrders() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                allOrders = api.getAllOrders()
                applyFilters()
            } catch (e: Exception) {
                Toast.makeText(this@AdminOrderListActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showStatusDialog(order: Order) {
        val currentStatusIndex = availableStatuses.indexOf(order.status)
        val checkedItem = if (currentStatusIndex >= 0) currentStatusIndex else 0

        AlertDialog.Builder(this)
            .setTitle("Pedido #${order.id}")
            .setSingleChoiceItems(availableStatuses, checkedItem) { dialog, which ->
                val newStatus = availableStatuses[which]
                updateOrderStatus(order, newStatus)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    private fun showDeleteConfirmation(order: Order) {
        AlertDialog.Builder(this)
            .setTitle("¿Borrar Pedido #${order.id}?")
            .setMessage("Esta acción no se puede deshacer.")
            .setPositiveButton("Borrar") { _, _ ->
                deleteOrder(order)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteOrder(order: Order) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = api.deleteOrder(order.id)
                if (response.isSuccessful) {
                    Toast.makeText(this@AdminOrderListActivity, "Pedido eliminado", Toast.LENGTH_SHORT).show()
                    loadOrders()
                } else {
                    Toast.makeText(this@AdminOrderListActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminOrderListActivity, "Fallo: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateOrderStatus(order: Order, newStatus: String) {
        lifecycleScope.launch {
            try {
                val body = mapOf("status" to newStatus)
                api.updateOrderStatus(order.id, body)
                Toast.makeText(this@AdminOrderListActivity, "Actualizado a: $newStatus", Toast.LENGTH_SHORT).show()
                loadOrders()
            } catch (e: Exception) {
                Toast.makeText(this@AdminOrderListActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}