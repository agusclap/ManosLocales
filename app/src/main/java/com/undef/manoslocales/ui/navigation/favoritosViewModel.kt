package com.undef.manoslocales.ui.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken // ¡IMPORTACIÓN CORRECTA DE GSON!
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
                Log.d("FavoritosViewModel", "Productos Favoritos Actualizados (Flow): ${it.size} elementos")
            }
            proveedoresFavoritos.collect {
                Log.d("FavoritosViewModel", "Proveedores Favoritos Actualizados (Flow): ${it.size} elementos")
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
                    Log.d("FavoritosViewModel", "Productos cargados desde SharedPreferences: ${loadedProducts.size} elementos")
                } catch (e: Exception) {
                    Log.e("FavoritosViewModel", "Error al deserializar productos: ${e.message}", e)
                    _productosFavoritos.value = emptyList() // Asegura que la lista esté vacía en caso de error
                }
            } else {
                Log.d("FavoritosViewModel", "No hay productos favoritos guardados en SharedPreferences.")
            }

            // Cargar proveedores
            val providersJson = sharedPreferences.getString(KEY_FAVORITE_PROVIDERS, null)
            if (providersJson != null) {
                try {
                    val type = object : TypeToken<List<User>>() {}.type
                    val loadedProviders = gson.fromJson<List<User>>(providersJson, type)
                    _proveedoresFavoritos.value = loadedProviders
                    Log.d("FavoritosViewModel", "Proveedores cargados desde SharedPreferences: ${loadedProviders.size} elementos")
                } catch (e: Exception) {
                    Log.e("FavoritosViewModel", "Error al deserializar proveedores: ${e.message}", e)
                    _proveedoresFavoritos.value = emptyList() // Asegura que la lista esté vacía en caso de error
                }
            } else {
                Log.d("FavoritosViewModel", "No hay proveedores favoritos guardados en SharedPreferences.")
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
        val current = _productosFavoritos.value.toMutableList()
        // Usa el ID del producto si lo tienes para una identificación más robusta
        if (current.any { it.id == producto.id }) { // Asumiendo que Product tiene un 'id' único
            current.removeAll { it.id == producto.id }
        } else {
            current.add(producto)
        }
        _productosFavoritos.value = current
        saveFavorites() // Guarda el estado después de cada cambio
    }

    fun toggleProveedorFavorito(proveedor: User) {
        val current = _proveedoresFavoritos.value.toMutableList()

        if (current.any { it.email == proveedor.email }) {
            current.removeAll { it.email == proveedor.email }
        } else {
            current.add(proveedor)
        }

        _proveedoresFavoritos.value = current
        saveFavorites() // Guarda el estado después de cada cambio
    }
}