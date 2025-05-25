package com.undef.manoslocales.ui.navigation

import androidx.lifecycle.ViewModel
import com.undef.manoslocales.ui.users.Producto
import com.undef.manoslocales.ui.users.Proveedor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.collections.toMutableList

class FavoritosViewModel : ViewModel() {

    private val _productosFavoritos = MutableStateFlow<List<Producto>>(emptyList())
    val productosFavoritos: StateFlow<List<Producto>> = _productosFavoritos

    private val _proveedoresFavoritos = MutableStateFlow<List<Proveedor>>(emptyList())
    val proveedoresFavoritos: StateFlow<List<Proveedor>> = _proveedoresFavoritos

    fun toggleProductoFavorito(producto: Producto) {
        val current = _productosFavoritos.value.toMutableList()
        if (current.any { it.id == producto.id }) {
            current.removeAll { it.id == producto.id }
        } else {
            current.add(producto)
        }
        _productosFavoritos.value = current
    }

    fun toggleProveedorFavorito(proveedor: Proveedor) {
        val current = _proveedoresFavoritos.value.toMutableList()
        if (current.any { it.id == proveedor.id }) {
            current.removeAll { it.id == proveedor.id }
        } else {
            current.add(proveedor)
        }
        _proveedoresFavoritos.value = current
    }
}




