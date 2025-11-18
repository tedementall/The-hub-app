package com.example.thehub.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thehub.data.model.Product
import com.example.thehub.data.repository.CartRepository
import com.example.thehub.data.repository.ProductRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: ProductRepository) : ViewModel() {

    // Lista de productos encontrados
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    // Estado de carga (para mostrar/ocultar el ProgressBar)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Variables para recordar el filtro actual
    private var currentQuery: String = ""
    private var currentCategory: String? = null

    /**
     * Realiza la búsqueda en Xano usando el repositorio.
     * @param query Texto a buscar (nombre del producto).
     * @param category Categoría seleccionada (o null para "Todos").
     */
    fun searchProducts(query: String = currentQuery, category: String? = currentCategory) {
        // Guardamos los valores actuales por si necesitamos recargar
        currentQuery = query
        currentCategory = category

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Si el texto está vacío, enviamos null para que Xano no filtre por nombre vacío
                val safeQuery = if (query.isBlank()) null else query

                val result = repository.getProducts(
                    q = safeQuery,
                    category = category
                )
                _products.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                _products.value = emptyList() // Si falla, limpiamos la lista
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Agrega el producto seleccionado al Carrito Global.
     * Llama directamente al objeto CartRepository para compartir los datos con el Inicio.
     */
    fun addToCart(product: Product, quantity: Int) {
        viewModelScope.launch {
            try {
                // ✅ CORRECCIÓN CLAVE:
                // Usamos CartRepository como objeto directo.
                // Esto asegura que se agregue a la misma lista que usa el resto de la app.
                CartRepository.addProductToCart(product, quantity)

                println("DEBUG: Agregado al Carrito Global -> ${product.name} x $quantity")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}