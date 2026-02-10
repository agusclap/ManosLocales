package com.undef.manoslocales.ui.notifications

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.dataclasses.Product
import kotlinx.coroutines.tasks.await

class FavoritesRepository {

    private val db = FirebaseFirestore.getInstance()
    private val fcm = FirebaseMessaging.getInstance()

    // --- ESCUCHAS REACTIVOS DE FAVORITOS ---
    
    fun listenToFavoriteProviderIds(userId: String, onUpdate: (List<String>) -> Unit): ListenerRegistration {
        return db.collection("users").document(userId)
            .collection("favoriteProviders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val ids = snapshot?.documents?.map { it.id } ?: emptyList()
                onUpdate(ids)
            }
    }

    fun listenToFavoriteProductIds(userId: String, onUpdate: (List<String>) -> Unit): ListenerRegistration {
        return db.collection("users").document(userId)
            .collection("favoriteProducts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val ids = snapshot?.documents?.map { it.id } ?: emptyList()
                onUpdate(ids)
            }
    }

    // --- OPERACIONES DE PRODUCTOS ---

    suspend fun getFavoriteProducts(userId: String): List<Product> {
        try {
            val favoriteIdsSnapshot = db.collection("users").document(userId)
                .collection("favoriteProducts").get().await()
            val favoriteProductIds = favoriteIdsSnapshot.documents.map { it.id }
            if (favoriteProductIds.isEmpty()) return emptyList()
            val productsSnapshot = db.collection("products")
                .whereIn("__name__", favoriteProductIds).get().await()
            return productsSnapshot.documents.mapNotNull { doc ->
                val product = doc.toObject(Product::class.java)
                product?.id = doc.id
                product
            }
        } catch (e: Exception) {
            return emptyList()
        }
    }

    suspend fun addProductToFavorites(userId: String, productId: String) {
        db.collection("users").document(userId)
            .collection("favoriteProducts").document(productId)
            .set(mapOf("addedAt" to FieldValue.serverTimestamp())).await()
        fcm.subscribeToTopic("product_$productId")
    }

    suspend fun removeProductFromFavorites(userId: String, productId: String) {
        db.collection("users").document(userId)
            .collection("favoriteProducts").document(productId)
            .delete().await()
        fcm.unsubscribeFromTopic("product_$productId")
    }

    // --- OPERACIONES DE PROVEEDORES ---

    suspend fun getFavoriteProviders(userId: String): List<User> {
        try {
            val favoriteIdsSnapshot = db.collection("users").document(userId)
                .collection("favoriteProviders").get().await()
            val favoriteProviderIds = favoriteIdsSnapshot.documents.map { it.id }
            if (favoriteProviderIds.isEmpty()) return emptyList()
            val providersSnapshot = db.collection("users")
                .whereIn("__name__", favoriteProviderIds).get().await()
            return providersSnapshot.documents.mapNotNull { doc ->
                val user = doc.toObject(User::class.java)
                user?.id = doc.id
                user
            }
        } catch (e: Exception) {
            return emptyList()
        }
    }

    suspend fun addProviderToFavorites(userId: String, providerId: String) {
        db.collection("users").document(userId)
            .collection("favoriteProviders").document(providerId)
            .set(mapOf("addedAt" to FieldValue.serverTimestamp())).await()
        fcm.subscribeToTopic("provider_$providerId")
    }

    suspend fun removeProviderFromFavorites(userId: String, providerId: String) {
        db.collection("users").document(userId)
            .collection("favoriteProviders").document(providerId)
            .delete().await()
        fcm.unsubscribeFromTopic("provider_$providerId")
    }

    // --- LISTENERS DE CAMBIOS EN PRODUCTOS ---

    fun listenToNewProductsFromProviders(
        providerIds: List<String>,
        startTime: Long,
        onNewProduct: (Product) -> Unit
    ): ListenerRegistration {
        return db.collection("products")
            .whereIn("providerId", providerIds)
            .whereGreaterThan("createdAt", startTime)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("NewProductListener", "Error: ${error.message}. ¿Falta el índice compuesto?")
                    return@addSnapshotListener
                }
                for (change in snapshots?.documentChanges ?: emptyList()) {
                    if (change.type == DocumentChange.Type.ADDED) {
                        val product = change.document.toObject(Product::class.java)
                        product.id = change.document.id
                        onNewProduct(product)
                    }
                }
            }
    }

    fun listenToProductChanges(productIds: List<String>, onProductUpdate: (Product) -> Unit): ListenerRegistration {
        return db.collection("products")
            .whereIn("__name__", productIds)
            .addSnapshotListener { snapshots, error ->
                if (error != null) return@addSnapshotListener
                for (change in snapshots?.documentChanges ?: emptyList()) {
                    if (change.type == DocumentChange.Type.MODIFIED) {
                        val product = change.document.toObject(Product::class.java)
                        product.id = change.document.id
                        onProductUpdate(product)
                    }
                }
            }
    }
}
