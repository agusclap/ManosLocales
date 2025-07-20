package com.undef.manoslocales.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.manoslocales.ui.data.SessionManager // Importa TU SessionManager
import com.undef.manoslocales.ui.dataclasses.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.notifications.FavoritesRepository

/**
 * ViewModel para gestionar el estado de los productos y proveedores favoritos.
 *
 * Esta clase ya NO usa SharedPreferences directamente. En su lugar:
 * 1.  Le pide a `SessionManager` el identificador del usuario logueado.
 * 2.  Usa `FavoritesRepository` para comunicarse con el servidor (backend).
 * 3.  Mantiene el estado de los favoritos en StateFlows para que la UI reaccione a los cambios.
 *
 * @param favoritesRepository El repositorio que maneja la comunicación de red para los favoritos.
 * @param sessionManager Tu gestor de sesión para saber quién está logueado.
 */
class FavoritosViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // StateFlow para la lista de productos favoritos. La UI observa este flow.
    private val _productosFavoritos = MutableStateFlow<List<Product>>(emptyList())
    val productosFavoritos: StateFlow<List<Product>> = _productosFavoritos

    // StateFlow para la lista de proveedores favoritos.
    private val _proveedoresFavoritos = MutableStateFlow<List<User>>(emptyList())
    val proveedoresFavoritos: StateFlow<List<User>> = _proveedoresFavoritos

    init {
        // Cuando el ViewModel se crea, intenta cargar los favoritos desde el servidor.
        loadFavoritesFromServer()
    }

    /**
     * Carga las listas de favoritos (productos y proveedores) desde el servidor
     * para el usuario que tiene la sesión iniciada.
     */
    private fun loadFavoritesFromServer() {
        viewModelScope.launch {
            // Primero, verificamos si hay un usuario logueado usando tu SessionManager.
            if (sessionManager.isLoggedIn()) {
                val userEmail = sessionManager.getLoggedInUserEmail()
                if (userEmail == null) {
                    Log.w("FAV_VM", "Usuario logueado pero sin email. No se pueden cargar favoritos.")
                    return@launch
                }

                try {
                    // Si todo está bien, le pedimos al repositorio que traiga los datos.
                    _productosFavoritos.value = favoritesRepository.getFavoriteProducts(userEmail)
                    _proveedoresFavoritos.value = favoritesRepository.getFavoriteProviders(userEmail)
                    Log.d("FAV_VM", "Favoritos cargados desde el servidor para el usuario: $userEmail")
                } catch (e: Exception) {
                    Log.e("FAV_VM", "Error al cargar favoritos desde el servidor", e)
                    // Aquí podrías emitir un estado de error para mostrar un mensaje en la UI.
                }
            } else {
                Log.i("FAV_VM", "No hay sesión iniciada. Las listas de favoritos estarán vacías.")
            }
        }
    }

    /**
     * Agrega o quita un producto de la lista de favoritos.
     * Implementa una "actualización optimista": la UI se actualiza al instante
     * y la llamada de red se hace en segundo plano. Si falla, se revierte el cambio.
     *
     * @param producto El producto en el que el usuario hizo clic.
     */
    fun toggleProductoFavorito(producto: Product) {
        viewModelScope.launch {
            val userEmail = sessionManager.getLoggedInUserEmail()
            if (userEmail == null) {
                Log.e("FAV_VM", "No se puede modificar favoritos. Usuario no logueado.")
                return@launch
            }

            val currentList = _productosFavoritos.value.toMutableList()
            val isCurrentlyFavorite = currentList.any { it.id == producto.id }

            // 1. Actualización Optimista: Modificamos la lista localmente para una UI fluida.
            if (isCurrentlyFavorite) {
                currentList.removeAll { it.id == producto.id }
            } else {
                currentList.add(producto)
            }
            _productosFavoritos.value = currentList

            // 2. Notificación al Servidor: Intentamos sincronizar el cambio con el backend.
            try {
                if (isCurrentlyFavorite) {
                    favoritesRepository.removeProductFromFavorites(userEmail, producto.id)
                    Log.d("FAV_VM", "Producto '${producto.name}' quitado de favoritos en el servidor.")
                } else {
                    favoritesRepository.addProductToFavorites(userEmail, producto.id)
                    Log.d("FAV_VM", "Producto '${producto.name}' agregado a favoritos en el servidor.")
                }
            } catch (e: Exception) {
                Log.e("FAV_VM", "Error de red al actualizar favorito. Revirtiendo cambio en UI.", e)
                // 3. Reversión: Si la llamada falla, deshacemos el cambio en la UI.
                val revertedList = _productosFavoritos.value.toMutableList()
                if (isCurrentlyFavorite) {
                    // Falló la eliminación, así que lo volvemos a agregar a la lista local.
                    revertedList.add(producto)
                } else {
                    // Falló la adición, así que lo quitamos de la lista local.
                    revertedList.removeAll { it.id == producto.id }
                }
                _productosFavoritos.value = revertedList
            }
        }
    }

    /**
     * Agrega o quita un proveedor de la lista de favoritos.
     * La lógica es idéntica a la de `toggleProductoFavorito`.
     *
     * @param proveedor El proveedor en el que el usuario hizo clic.
     */
    fun toggleProveedorFavorito(proveedor: User) {
        viewModelScope.launch {
            val userEmail = sessionManager.getLoggedInUserEmail()
            if (userEmail == null) {
                Log.e("FAV_VM", "No se puede modificar favoritos. Usuario no logueado.")
                return@launch
            }

            val currentList = _proveedoresFavoritos.value.toMutableList()
            val isCurrentlyFavorite = currentList.any { it.email == proveedor.email }

            // 1. Actualización Optimista
            if (isCurrentlyFavorite) {
                currentList.removeAll { it.email == proveedor.email }
            } else {
                currentList.add(proveedor)
            }
            _proveedoresFavoritos.value = currentList

            // 2. Notificación al Servidor
            try {
                if (isCurrentlyFavorite) {
                    favoritesRepository.removeProviderFromFavorites(userEmail, proveedor.email) // Asumiendo que el ID del proveedor es su email
                    Log.d("FAV_VM", "Proveedor '${proveedor.nombre}' quitado de favoritos.")
                } else {
                    favoritesRepository.addProviderToFavorites(userEmail, proveedor.email)
                    Log.d("FAV_VM", "Proveedor '${proveedor.nombre}' agregado a favoritos.")
                }
            } catch (e: Exception) {
                Log.e("FAV_VM", "Error de red al actualizar proveedor favorito. Revirtiendo.", e)
                // 3. Reversión
                val revertedList = _proveedoresFavoritos.value.toMutableList()
                if (isCurrentlyFavorite) {
                    revertedList.add(proveedor)
                } else {
                    revertedList.removeAll { it.email == proveedor.email }
                }
                _proveedoresFavoritos.value = revertedList
            }
        }
    }
}
