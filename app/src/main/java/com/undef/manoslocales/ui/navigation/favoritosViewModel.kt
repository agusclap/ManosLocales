package com.undef.manoslocales.ui.navigation

import androidx.lifecycle.ViewModel
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.dataclasses.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoritosViewModel : ViewModel() {

    private val _productosFavoritos = MutableStateFlow<List<Product>>(emptyList())
    val productosFavoritos: StateFlow<List<Product>> = _productosFavoritos

    private val _proveedoresFavoritos = MutableStateFlow<List<User>>(emptyList())
    val proveedoresFavoritos: StateFlow<List<User>> = _proveedoresFavoritos

    fun toggleProductoFavorito(producto: Product) {
        val current = _productosFavoritos.value.toMutableList()
        if (current.any { it.name == producto.name && it.description == producto.description }) {
            current.removeAll { it.name == producto.name && it.description == producto.description }
        } else {
            current.add(producto)
        }
        _productosFavoritos.value = current
    }

    fun toggleProveedorFavorito(proveedor: User) {
        val current = _proveedoresFavoritos.value.toMutableList()
        if (current.any { it.id == proveedor.id }) {
            current.removeAll { it.id == proveedor.id }
        } else {
            current.add(proveedor)
        }
        _proveedoresFavoritos.value = current
    }
}
