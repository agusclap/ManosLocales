package com.undef.manoslocales.ui.navigation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.manoslocales.ui.data.SessionManager // Importa TU SessionManager
import com.undef.manoslocales.ui.dataclasses.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.notifications.FavoritesRepository
import com.undef.manoslocales.ui.notifications.NotificationHelper

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
    application: Application,
    private val favoritesRepository: FavoritesRepository,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val _productosFavoritos = MutableStateFlow<List<Product>>(emptyList())
    val productosFavoritos: StateFlow<List<Product>> = _productosFavoritos

    private val _proveedoresFavoritos = MutableStateFlow<List<User>>(emptyList())
    val proveedoresFavoritos: StateFlow<List<User>> = _proveedoresFavoritos

    // Listeners para los cambios en tiempo real
    private var priceListener: ListenerRegistration? = null
    private var newProductListener: ListenerRegistration? = null
    private val productPrices = mutableMapOf<String, Double>()

    // --- CICLO DE VIDA Y CARGA DE DATOS ---

    fun loadFavoritesForCurrentUser() {
        val userId = sessionManager.getUserId()
        if (userId == null) {
            clearFavorites() // Si no hay usuario, nos aseguramos de limpiar todo.
            return
        }
        viewModelScope.launch {
            try {
                _productosFavoritos.value = favoritesRepository.getFavoriteProducts(userId)
                _proveedoresFavoritos.value = favoritesRepository.getFavoriteProviders(userId)
                Log.d("FAV_VM", "Favoritos cargados para: $userId")

                // Una vez cargados los favoritos, iniciamos ambos "escuchas".
                startPriceChangeListener()
                startNewProductListener()
            } catch (e: Exception) {
                Log.e("FAV_VM", "Error al cargar favoritos", e)
                clearFavorites()
            }
        }
    }

    fun clearFavorites() {
        _productosFavoritos.value = emptyList()
        _proveedoresFavoritos.value = emptyList()
        stopPriceChangeListener()
        stopNewProductListener()
    }

    override fun onCleared() {
        super.onCleared()
        // Es crucial detener los listeners cuando el ViewModel se destruye para evitar fugas de memoria.
        stopPriceChangeListener()
        stopNewProductListener()
    }

    // --- LÓGICA DE "ESCUCHAS" EN TIEMPO REAL ---

    private fun startPriceChangeListener() {
        val favoriteProducts = _productosFavoritos.value
        if (favoriteProducts.isEmpty()) {
            Log.d("PriceListener", "No hay productos favoritos para vigilar precios.")
            return
        }
        stopPriceChangeListener() // Evitar listeners duplicados.

        val favoriteProductIds = favoriteProducts.map { product ->
            productPrices[product.id] = product.price
            product.id
        }

        Log.d("PriceListener", "Iniciando escucha para ${favoriteProductIds.size} productos.")
        priceListener = favoritesRepository.listenToProductChanges(favoriteProductIds) { updatedProduct ->
            val oldPrice = productPrices[updatedProduct.id]
            val newPrice = updatedProduct.price
            Log.d("PriceListener", "Cambio detectado para ${updatedProduct.name}. Antes: $oldPrice, Ahora: $newPrice")

            if (oldPrice != null && newPrice != oldPrice) {
                Log.i("PriceListener", "¡CAMBIO DE PRECIO! Notificando al usuario.")
                val changeType = if (newPrice > oldPrice) "subió" else "bajó"
                NotificationHelper.showPriceChangeNotification(
                    context = getApplication(),
                    productName = updatedProduct.name,
                    newPrice = newPrice,
                    changeType = changeType
                )
            }
            productPrices[updatedProduct.id] = newPrice
        }
    }

    private fun stopPriceChangeListener() {
        priceListener?.remove()
        priceListener = null
        productPrices.clear()
        Log.d("PriceListener", "Escucha de precios detenido.")
    }

    // --- IMPLEMENTACIÓN DE LA LÓGICA FALTANTE ---

    private fun startNewProductListener() {
        val favoriteProviders = _proveedoresFavoritos.value
        if (favoriteProviders.isEmpty()) {
            Log.d("NewProductListener", "No hay proveedores favoritos para vigilar.")
            return
        }
        stopNewProductListener() // Evitar listeners duplicados.

        val favoriteProviderIds = favoriteProviders.map { it.id }
        val startTime = System.currentTimeMillis()

        Log.d("NewProductListener", "Iniciando escucha para nuevos productos de ${favoriteProviderIds.size} proveedores.")
        newProductListener = favoritesRepository.listenToNewProductsFromProviders(
            favoriteProviderIds,
            startTime
        ) { newProduct ->
            val provider = favoriteProviders.find { it.id == newProduct.providerId }
            val providerName = provider?.nombre ?: "Tu proveedor favorito"

            Log.i("NewProductListener", "¡NUEVO PRODUCTO DETECTADO! Notificando al usuario.")
            NotificationHelper.showNewProductNotification(
                context = getApplication(),
                providerName = providerName,
                productName = newProduct.name
            )
        }
    }

    private fun stopNewProductListener() {
        newProductListener?.remove()
        newProductListener = null
        Log.d("NewProductListener", "Escucha de nuevos productos detenido.")
    }

    fun toggleProductoFavorito(producto: Product) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId() ?: return@launch

            val currentList = _productosFavoritos.value.toMutableList()
            val isCurrentlyFavorite = currentList.any { it.id == producto.id }

            if (isCurrentlyFavorite) {
                currentList.removeAll { it.id == producto.id }
            } else {
                currentList.add(producto)
            }
            _productosFavoritos.value = currentList

            try {
                if (isCurrentlyFavorite) {
                    favoritesRepository.removeProductFromFavorites(userId, producto.id)
                } else {
                    favoritesRepository.addProductToFavorites(userId, producto.id)
                }
            } catch (e: Exception) {
                Log.e("FAV_VM", "Error de red al actualizar producto favorito. Revirtiendo.", e)
                // Reversión
                if (isCurrentlyFavorite) _productosFavoritos.value += producto
                else _productosFavoritos.value = _productosFavoritos.value.filter { it.id != producto.id }
            }
        }
    }

    fun toggleProveedorFavorito(proveedor: User) {
        viewModelScope.launch {
            // CORRECCIÓN: Usamos getUserId() en lugar de getLoggedInUserEmail()
            val userId = sessionManager.getUserId()
            if (userId == null) {
                Log.e("FAV_VM", "No se puede modificar favoritos. Usuario no logueado.")
                return@launch
            }

            // CORRECCIÓN: Usamos el ID (UID) del proveedor, no su email
            val providerId = proveedor.id

            val currentList = _proveedoresFavoritos.value.toMutableList()
            // CORRECCIÓN: Comparamos por ID, no por email
            val isCurrentlyFavorite = currentList.any { it.id == providerId }

            if (isCurrentlyFavorite) {
                currentList.removeAll { it.id == providerId }
            } else {
                currentList.add(proveedor)
            }
            _proveedoresFavoritos.value = currentList

            try {
                if (isCurrentlyFavorite) {
                    // CORRECCIÓN: Pasamos los UID (userId y providerId) al repositorio
                    favoritesRepository.removeProviderFromFavorites(userId, providerId)
                } else {
                    // CORRECCIÓN: Pasamos los UID (userId y providerId) al repositorio
                    favoritesRepository.addProviderToFavorites(userId, providerId)
                }
            } catch (e: Exception) {
                Log.e("FAV_VM", "Error de red al actualizar proveedor favorito. Revirtiendo.", e)
                // Reversión...
            }
        }
    }
}
