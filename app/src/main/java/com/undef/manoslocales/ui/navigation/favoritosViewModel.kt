package com.undef.manoslocales.ui.navigation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.manoslocales.ui.data.SessionManager
import com.undef.manoslocales.ui.data.SettingsManager // Importamos el SettingsManager
import com.undef.manoslocales.ui.dataclasses.Product
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.notifications.FavoritesRepository
import com.undef.manoslocales.ui.notifications.NotificationHelper
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavoritosViewModel(
    application: Application,
    private val favoritesRepository: FavoritesRepository,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    // --- 1. AÑADIMOS EL GESTOR DE AJUSTES ---
    private val settingsManager = SettingsManager(application)

    private val _productosFavoritos = MutableStateFlow<List<Product>>(emptyList())
    val productosFavoritos: StateFlow<List<Product>> = _productosFavoritos

    private val _proveedoresFavoritos = MutableStateFlow<List<User>>(emptyList())
    val proveedoresFavoritos: StateFlow<List<User>> = _proveedoresFavoritos

    private var priceListener: ListenerRegistration? = null
    private var newProductListener: ListenerRegistration? = null
    private val productPrices = mutableMapOf<String, Double>()

    fun loadFavoritesForCurrentUser() {
        val userId = sessionManager.getUserId()
        if (userId == null) {
            clearFavorites()
            return
        }

        viewModelScope.launch {
            try {
                val favoriteProducts = favoritesRepository.getFavoriteProducts(userId)
                val favoriteProviders = favoritesRepository.getFavoriteProviders(userId)

                _productosFavoritos.value = favoriteProducts
                _proveedoresFavoritos.value = favoriteProviders

                // Iniciamos los "escuchas" después de cargar los datos.
                startPriceChangeListener(favoriteProducts)
                startNewProductListener(favoriteProviders)

                Log.d("FAV_VM", "Favoritos cargados. Productos: ${favoriteProducts.size}, Proveedores: ${favoriteProviders.size}")
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
        stopPriceChangeListener()
        stopNewProductListener()
    }

    // --- 2. MODIFICAMOS LOS "ESCUCHAS" PARA QUE COMPRUEBEN LOS AJUSTES ---

    private fun startPriceChangeListener(favoriteProducts: List<Product>) {
        if (favoriteProducts.isEmpty()) {
            Log.d("PriceListener", "No hay productos favoritos para vigilar.")
            return
        }
        stopPriceChangeListener()

        val favoriteProductIds = favoriteProducts.map { product ->
            productPrices[product.id] = product.price
            product.id
        }

        Log.d("PriceListener", "Iniciando escucha para ${favoriteProductIds.size} productos.")
        priceListener = favoritesRepository.listenToProductChanges(favoriteProductIds) { updatedProduct ->
            viewModelScope.launch {
                // Leemos el valor actual de la preferencia
                val notificationsEnabled = settingsManager.priceNotificationsEnabledFlow.first()

                if (!notificationsEnabled) {
                    Log.d("PriceListener", "Cambio de precio detectado, pero las notificaciones están DESACTIVADAS.")
                    return@launch // Detenemos la ejecución aquí
                }

                val oldPrice = productPrices[updatedProduct.id]
                val newPrice = updatedProduct.price
                if (oldPrice != null && newPrice != oldPrice) {
                    val changeType = if (newPrice > oldPrice) "subió" else "bajó"
                    Log.i("PriceListener", "¡CAMBIO DE PRECIO! Notificando al usuario.")
                    NotificationHelper.showPriceChangeNotification(
                        context = getApplication(),
                        productId = updatedProduct.id,
                        productName = updatedProduct.name,
                        newPrice = newPrice,
                        changeType = changeType
                    )
                }
                productPrices[updatedProduct.id] = newPrice
            }
        }
    }

    private fun stopPriceChangeListener() {
        priceListener?.remove()
        priceListener = null
        productPrices.clear()
    }

    private fun startNewProductListener(favoriteProviders: List<User>) {
        if (favoriteProviders.isEmpty()) {
            Log.d("NewProductListener", "No hay proveedores favoritos para vigilar.")
            return
        }
        stopNewProductListener()

        val favoriteProviderIds = favoriteProviders.map { it.id }
        val startTime = System.currentTimeMillis()

        newProductListener = favoritesRepository.listenToNewProductsFromProviders(
            favoriteProviderIds, startTime
        ) { newProduct ->
            viewModelScope.launch {
                val notificationsEnabled = settingsManager.newProductNotificationsEnabledFlow.first()

                if (!notificationsEnabled) {
                    Log.d("NewProductListener", "Nuevo producto detectado, pero las notificaciones están DESACTIVADAS.")
                    return@launch
                }

                val provider = favoriteProviders.find { it.id == newProduct.providerId }
                val providerName = provider?.nombre ?: "Tu proveedor favorito"
                Log.i("NewProductListener", "¡NUEVO PRODUCTO DETECTADO! Notificando al usuario.")
                NotificationHelper.showNewProductNotification(
                    context = getApplication(),
                    productId = newProduct.id,
                    providerName = providerName,
                    productName = newProduct.name
                )
            }
        }
    }

    private fun stopNewProductListener() {
        newProductListener?.remove()
        newProductListener = null
    }

    fun toggleProductoFavorito(producto: Product) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId() ?: return@launch
            val currentList = _productosFavoritos.value.toMutableList()
            val isCurrentlyFavorite = currentList.any { it.id == producto.id }

            if (isCurrentlyFavorite) currentList.removeAll { it.id == producto.id }
            else currentList.add(producto)
            _productosFavoritos.value = currentList

            try {
                if (isCurrentlyFavorite) favoritesRepository.removeProductFromFavorites(userId, producto.id)
                else favoritesRepository.addProductToFavorites(userId, producto.id)
            } catch (e: Exception) {
                Log.e("FAV_VM", "Error al actualizar producto favorito. Revirtiendo.", e)
                loadFavoritesForCurrentUser() // Recargamos para asegurar consistencia
            }
        }
    }

    fun toggleProveedorFavorito(proveedor: User) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId() ?: return@launch
            val currentList = _proveedoresFavoritos.value.toMutableList()
            val isCurrentlyFavorite = currentList.any { it.id == proveedor.id }

            if (isCurrentlyFavorite) currentList.removeAll { it.id == proveedor.id }
            else currentList.add(proveedor)
            _proveedoresFavoritos.value = currentList

            try {
                if (isCurrentlyFavorite) favoritesRepository.removeProviderFromFavorites(userId, proveedor.id)
                else favoritesRepository.addProviderToFavorites(userId, proveedor.id)
            } catch (e: Exception) {
                Log.e("FAV_VM", "Error al actualizar proveedor favorito. Revirtiendo.", e)
                loadFavoritesForCurrentUser()
            }
        }
    }
}
