package com.example.thehub.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thehub.R
import com.example.thehub.data.model.User
import com.example.thehub.databinding.ActivityAdminProductListBinding
import com.example.thehub.di.ServiceLocator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AdminUserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminProductListBinding
    private val userRepository = ServiceLocator.userRepository
    private lateinit var adapter: AdminUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadUsers()
    }

    override fun onResume() {
        super.onResume()
        loadUsers()
    }

    private fun setupUI() {
        // Ajustamos el título
        val titleView = binding.header.getChildAt(1) as? TextView
        titleView?.text = "Gestionar Usuarios"

        binding.btnBack.setOnClickListener { finish() }


        adapter = AdminUserAdapter(
            users = emptyList(),
            onEditClick = { user ->

                val intent = Intent(this, AdminEditUserActivity::class.java)
                intent.putExtra("extra_user", user)
                startActivity(intent)
            },
            onDeleteClick = { user ->
                confirmDelete(user)
            }
        )

        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            try {
                val users = userRepository.getAllUsers()
                adapter.updateList(users)
            } catch (e: Exception) {
                Toast.makeText(this@AdminUserListActivity, "Error cargando usuarios", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun confirmDelete(user: User) {
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Eliminar usuario?")
            .setMessage("Se eliminará la cuenta de ${user.nombre}. Esta acción es irreversible.")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                deleteUser(user)
            }
            .show()
    }

    private fun deleteUser(user: User) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            try {
                val response = userRepository.deleteUser(user.id)
                if (response.isSuccessful) {
                    Toast.makeText(this@AdminUserListActivity, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                    loadUsers()
                } else {
                    Toast.makeText(this@AdminUserListActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminUserListActivity, "Fallo de conexión", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}