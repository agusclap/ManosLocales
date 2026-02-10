package com.undef.manoslocales.ui.notifications

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.undef.manoslocales.ui.dataclasses.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.ListenerRegistration

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val favoritesRepository = FavoritesRepository()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var notificationsListener: ListenerRegistration? = null
    private var newProductsListener: ListenerRegistration? = null
    private var priceChangesListener: ListenerRegistration? = null
    
    // Listeners para detectar cambios en la lista de favoritos
    private var favProvidersListener: ListenerRegistration? = null
    private var favProductsListener: ListenerRegistration? = null

    private val notifiedProductIds = mutableSetOf<String>()
    val notifications = mutableStateListOf<NotificationItem>()
    
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount
    
    data class NotificationItem(
        val id: String = "",
        val title: String = "",
        val message: String = "",
        val productId: String = "",
        val timestamp: Long = System.currentTimeMillis(),
        val read: Boolean = false
    )

    fun startListening() {
        val userId = auth.currentUser?.uid ?: return
        
        stopListening()
        loadPersistentNotifications(userId)

        // Escucha reactiva: Si agregas un favorito nuevo, reiniciamos los filtros automáticamente
        favProvidersListener = favoritesRepository.listenToFavoriteProviderIds(userId) { providerIds ->
            setupNewProductListener(userId, providerIds)
        }

        favProductsListener = favoritesRepository.listenToFavoriteProductIds(userId) { productIds ->
            setupPriceChangeListener(userId, productIds)
        }
    }

    private fun setupNewProductListener(userId: String, providerIds: List<String>) {
        newProductsListener?.remove()
        if (providerIds.isEmpty()) return

        val safeStartTime = System.currentTimeMillis() - (2 * 60 * 1000)
        newProductsListener = favoritesRepository.listenToNewProductsFromProviders(
            providerIds, safeStartTime
        ) { product ->
            if (!notifiedProductIds.contains(product.id)) {
                notifiedProductIds.add(product.id)
                saveAndShowNotification(userId, "¡Nuevo producto!", "${product.name} publicado por proveedor favorito", product.id)
            }
        }
    }

    private fun setupPriceChangeListener(userId: String, productIds: List<String>) {
        priceChangesListener?.remove()
        if (productIds.isEmpty()) return

        // Cargamos precios actuales para comparar
        viewModelScope.launch {
            val currentProducts = favoritesRepository.getFavoriteProducts(userId)
            val productPrices = currentProducts.associate { it.id to it.price }.toMutableMap()

            priceChangesListener = favoritesRepository.listenToProductChanges(productIds) { updatedProduct ->
                val oldPrice = productPrices[updatedProduct.id]
                if (oldPrice != null && updatedProduct.price != oldPrice) {
                    val change = if (updatedProduct.price > oldPrice) "subió" else "bajó"
                    saveAndShowNotification(userId, "¡Cambio de precio!", "El precio de ${updatedProduct.name} $change a $${updatedProduct.price}", updatedProduct.id)
                    productPrices[updatedProduct.id] = updatedProduct.price
                }
            }
        }
    }

    private fun loadPersistentNotifications(userId: String) {
        notificationsListener = db.collection("user_notifications")
            .document(userId).collection("items")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    notifications.clear()
                    val items = snapshot.documents.mapNotNull { it.toObject(NotificationItem::class.java)?.copy(id = it.id) }
                    notifications.addAll(items)
                    _unreadCount.value = items.count { !it.read }
                }
            }
    }

    fun stopListening() {
        notificationsListener?.remove()
        newProductsListener?.remove()
        priceChangesListener?.remove()
        favProvidersListener?.remove()
        favProductsListener?.remove()
    }

    fun clearNotifications() {
        stopListening()
        notifications.clear()
        _unreadCount.value = 0
        notifiedProductIds.clear()
    }

    fun markAllAsRead() {
        val userId = auth.currentUser?.uid ?: return
        val unreadNotifications = notifications.filter { !it.read }
        if (unreadNotifications.isEmpty()) return

        viewModelScope.launch {
            val batch = db.batch()
            unreadNotifications.forEach { notif ->
                val docRef = db.collection("user_notifications")
                    .document(userId).collection("items").document(notif.id)
                batch.update(docRef, "read", true)
            }
            try {
                batch.commit()
            } catch (e: Exception) {
                Log.e("NotificationVM", "Error marking notifications as read", e)
            }
        }
    }

    private fun saveAndShowNotification(userId: String, title: String, message: String, productId: String) {
        val item = NotificationItem(title = title, message = message, productId = productId)
        db.collection("user_notifications").document(userId).collection("items").add(item)
        NotificationHelper.sendProductNotification(getApplication(), productId, title, message)
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}
