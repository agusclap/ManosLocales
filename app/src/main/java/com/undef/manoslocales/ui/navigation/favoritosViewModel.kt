package com.undef.manoslocales.ui.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken // ¡Importación correcta de Gson!
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.dataclasses.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log // Para depuración

class FavoritosViewModel(application: Application) : AndroidViewModel(application) {

    private val PREFS_NAME = "favorite_prefs"
    private val KEY_FAVORITE_PRODUCTS = "favorite_products"
    private val KEY_FAVORITE_PROVIDERS = "favorite_providers"

    private val sharedPreferences = application.getSharedPreferences(PREFS_NAME, Application.MODE_PRIVATE)
    private val gson = Gson()

    private val _productosFavoritos = MutableStateFlow<List<Product>>(emptyList())
    val productosFavoritos: StateFlow<List<Product>> = _productosFavoritos

    private val _proveedoresFavoritos = MutableStateFlow<List<User>>(emptyList())
    val proveedoresFavoritos: StateFlow<List<User>> = _proveedoresFavoritos

    init {
        // Cargar favoritos al inicializar el ViewModel
        loadFavorites()
        viewModelScope.launch {
            productosFavoritos.collect {
                Log.d("FAV_VM", "Productos Favoritos Actualizados (Flow): ${it.size} elementos. Contenido: ${it.map { p -> p.name }}")
            }
            proveedoresFavoritos.collect {
                Log.d("FAV_VM", "Proveedores Favoritos Actualizados (Flow): ${it.size} elementos. Contenido: ${it.map { u -> u.nombre }}")
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            // Cargar productos
            val productsJson = sharedPreferences.getString(KEY_FAVORITE_PRODUCTS, null)
            if (productsJson != null) {
                try {
                    val type = object : TypeToken<List<Product>>() {}.type
                    val loadedProducts = gson.fromJson<List<Product>>(productsJson, type)
                    _productosFavoritos.value = loadedProducts
                    Log.d("FAV_VM", "Productos cargados desde SharedPreferences: ${loadedProducts.size} elementos. Contenido: ${loadedProducts.map { p -> p.name }}")
                } catch (e: Exception) {
                    Log.e("FAV_VM", "Error al deserializar productos: ${e.message}", e)
                    _productosFavoritos.value = emptyList() // Asegura que la lista esté vacía en caso de error
                }
            } else {
                Log.d("FAV_VM", "No hay productos favoritos guardados en SharedPreferences.")
            }

            // Cargar proveedores
            val providersJson = sharedPreferences.getString(KEY_FAVORITE_PROVIDERS, null)
            if (providersJson != null) {
                try {
                    val type = object : TypeToken<List<User>>() {}.type
                    val loadedProviders = gson.fromJson<List<User>>(providersJson, type)
                    _proveedoresFavoritos.value = loadedProviders
                    Log.d("FAV_VM", "Proveedores cargados desde SharedPreferences: ${loadedProviders.size} elementos. Contenido: ${loadedProviders.map { u -> u.nombre }}")
                } catch (e: Exception) {
                    Log.e("FAV_VM", "Error al deserializar proveedores: ${e.message}", e)
                    _proveedoresFavoritos.value = emptyList() // Asegura que la lista esté vacía en caso de error
                }
            } else {
                Log.d("FAV_VM", "No hay proveedores favoritos guardados en SharedPreferences.")
            }
        }
    }

    private fun saveFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            // Guardar productos
            val productsJson = gson.toJson(_productosFavoritos.value)
            sharedPreferences.edit().putString(KEY_FAVORITE_PRODUCTS, productsJson).apply()

            // Guardar proveedores
            val providersJson = gson.toJson(_proveedoresFavoritos.value)
            sharedPreferences.edit().putString(KEY_FAVORITE_PROVIDERS, providersJson).apply()
        }
    }

    fun toggleProductoFavorito(producto: Product) {
        Log.d("FAV_VM", "toggleProductoFavorito llamado para: ${producto.name} (ID: ${producto.id})")
        val current = _productosFavoritos.value.toMutableList()
        val isCurrentlyFavorite = current.any { it.id == producto.id } // ¡Usamos el ID para comparar!
        if (isCurrentlyFavorite) {
            current.removeAll { it.id == producto.id } // ¡Usamos el ID para remover!
            Log.d("FAV_VM", "Producto removido. Nueva lista: ${current.size}")
        } else {
            current.add(producto)
            Log.d("FAV_VM", "Producto añadido. Nueva lista: ${current.size}")
        }
        _productosFavoritos.value = current
        saveFavorites() // Guarda el estado después de cada cambio
    }

    fun toggleProveedorFavorito(proveedor: User) {
        Log.d("FAV_VM", "toggleProveedorFavorito llamado para: ${proveedor.nombre} (Email: ${proveedor.email})")
        val current = _proveedoresFavoritos.value.toMutableList()

        if (current.any { it.email == proveedor.email }) { // El email suele ser un buen ID único para usuarios
            current.removeAll { it.email == proveedor.email }
            Log.d("FAV_VM", "Proveedor removido. Nueva lista: ${current.size}")
        } else {
            current.add(proveedor)
            Log.d("FAV_VM", "Proveedor añadido. Nueva lista: ${current.size}")
        }

        _proveedoresFavoritos.value = current
        saveFavorites() // Guarda el estado después de cada cambio
    }
}