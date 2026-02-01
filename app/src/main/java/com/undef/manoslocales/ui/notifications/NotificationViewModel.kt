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
import kotlinx.coroutines.tasks.await

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val favoritesRepository = FavoritesRepository()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
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
        
        // Cargar notificaciones persistentes desde Firestore
        loadPersistentNotifications(userId)

        viewModelScope.launch {
            // 1. Escuchar nuevos productos de proveedores favoritos
            val favoriteProviders = favoritesRepository.getFavoriteProviders(userId)
            val providerIds = favoriteProviders.map { it.id }
            
            if (providerIds.isNotEmpty()) {
                favoritesRepository.listenToNewProductsFromProviders(
                    providerIds,
                    System.currentTimeMillis()
                ) { product ->
                    val title = "¡Nuevo producto!"
                    val message = "${product.name} ha sido publicado por un proveedor favorito."
                    
                    saveAndShowNotification(userId, title, message, product.id)
                }
            }

            // 2. Escuchar cambios en productos favoritos (Precio)
            val favoriteProducts = favoritesRepository.getFavoriteProducts(userId)
            val productIds = favoriteProducts.map { it.id }
            
            if (productIds.isNotEmpty()) {
                val productPrices = favoriteProducts.associate { it.id to it.price }.toMutableMap()
                
                favoritesRepository.listenToProductChanges(productIds) { updatedProduct ->
                    val oldPrice = productPrices[updatedProduct.id]
                    if (oldPrice != null && updatedProduct.price != oldPrice) {
                        val changeType = if (updatedProduct.price > oldPrice) "subió" else "bajó"
                        val title = "¡Cambio de precio!"
                        val message = "El precio de ${updatedProduct.name} $changeType a $${updatedProduct.price}"
                        
                        saveAndShowNotification(userId, title, message, updatedProduct.id)
                        productPrices[updatedProduct.id] = updatedProduct.price
                    }
                }
            }
        }
    }

    private fun loadPersistentNotifications(userId: String) {
        db.collection("user_notifications")
            .document(userId)
            .collection("items")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("NotifVM", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    notifications.clear()
                    val items = snapshot.documents.mapNotNull { it.toObject(NotificationItem::class.java)?.copy(id = it.id) }
                    notifications.addAll(items)
                    _unreadCount.value = items.count { !it.read }
                }
            }
    }

    private fun saveAndShowNotification(userId: String, title: String, message: String, productId: String) {
        val notificationItem = NotificationItem(
            title = title,
            message = message,
            productId = productId,
            timestamp = System.currentTimeMillis(),
            read = false
        )

        // Tarea 3: Persistencia en Firestore
        db.collection("user_notifications")
            .document(userId)
            .collection("items")
            .add(notificationItem)

        // Tarea 2: Disparar notificación de sistema (usa canal_precios definido en Helper)
        NotificationHelper.sendProductNotification(
            getApplication(),
            productId,
            title,
            message
        )
    }
    
    fun markAllAsRead() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val batch = db.batch()
            notifications.filter { !it.read }.forEach { notif ->
                val docRef = db.collection("user_notifications")
                    .document(userId)
                    .collection("items")
                    .document(notif.id)
                batch.update(docRef, "read", true)
            }
            batch.commit()
        }
    }

    fun clearNotifications() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val batch = db.batch()
            notifications.forEach { notif ->
                val docRef = db.collection("user_notifications")
                    .document(userId)
                    .collection("items")
                    .document(notif.id)
                batch.delete(docRef)
            }
            batch.commit()
        }
    }
}
