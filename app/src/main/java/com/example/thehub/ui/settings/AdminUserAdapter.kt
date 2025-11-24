package com.example.thehub.ui.settings

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thehub.data.model.User
import com.example.thehub.databinding.ItemAdminUserBinding

class AdminUserAdapter(
    private var users: List<User>,
    private val onEditClick: (User) -> Unit, // 👈 NUEVO PARÁMETRO AGREGADO
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemAdminUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemAdminUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        with(holder.binding) {
            tvUserName.text = user.nombre
            tvUserEmail.text = user.correo

            // Lógica de roles (usando tus nombres en español)
            if (user.tipoUsuario == "admin") {
                tvRole.text = "ADMINISTRADOR"
                tvRole.setTextColor(Color.parseColor("#3F51B5"))
                tvRole.background.setTint(Color.parseColor("#E8EAF6"))
            } else {
                tvRole.text = "CLIENTE"
                tvRole.setTextColor(Color.parseColor("#616161"))
                tvRole.background.setTint(Color.parseColor("#F5F5F5"))
            }

            // Conectar los botones
            btnDelete.setOnClickListener { onDeleteClick(user) }
            btnEdit.setOnClickListener { onEditClick(user) } // 👈 USAR EL NUEVO PARÁMETRO
        }
    }

    override fun getItemCount() = users.size

    fun updateList(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}