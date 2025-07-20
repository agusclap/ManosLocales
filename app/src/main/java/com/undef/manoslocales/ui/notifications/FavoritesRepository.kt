package com.undef.manoslocales.ui.notifications // O la ruta que prefieras

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.undef.manoslocales.ui.dataclasses.Product
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.undef.manoslocales.ui.database.User

class FavoritesRepository {

    private val db = FirebaseFirestore.getInstance()

    // --- PRODUCTOS ---
    suspend fun getFavoriteProducts(userId: String): List<Product> {
        // ... (El código de esta función es el mismo que te pasé en la respuesta anterior)
        try {
            val favoriteIdsSnapshot = db.collection("users").document(userId)
                .collection("favoriteProducts").get().await()
            val favoriteProductIds = favoriteIdsSnapshot.documents.map { it.id }
            if (favoriteProductIds.isEmpty()) return emptyList()
            val productsSnapshot = db.collection("products")
                .whereIn("__name__", favoriteProductIds).get().await()
            return productsSnapshot.toObjects<Product>()
        } catch (e: Exception) {
            Log.e("FAV_REPO", "Error al obtener productos favoritos", e)
            return emptyList()
        }
    }

    suspend fun addProductToFavorites(userId: String, productId: String) {
        db.collection("users").document(userId)
            .collection("favoriteProducts").document(productId)
            .set(mapOf("addedAt" to FieldValue.serverTimestamp())).await()
    }

    suspend fun removeProductFromFavorites(userId: String, productId: String) {
        db.collection("users").document(userId)
            .collection("favoriteProducts").document(productId)
            .delete().await()
    }

    // --- PROVEEDORES ---
    suspend fun getFavoriteProviders(userId: String): List<User> {
        // ... (El código de esta función es el mismo que te pasé en la respuesta anterior)
        try {
            val favoriteIdsSnapshot = db.collection("users").document(userId)
                .collection("favoriteProviders").get().await()
            val favoriteProviderIds = favoriteIdsSnapshot.documents.map { it.id }
            if (favoriteProviderIds.isEmpty()) return emptyList()
            val providersSnapshot = db.collection("users")
                .whereIn("__name__", favoriteProviderIds).get().await()
            return providersSnapshot.toObjects<User>()
        } catch (e: Exception) {
            Log.e("FAV_REPO", "Error al obtener proveedores favoritos", e)
            return emptyList()
        }
    }

    suspend fun addProviderToFavorites(userId: String, providerId: String) {
        db.collection("users").document(userId)
            .collection("favoriteProviders").document(providerId)
            .set(mapOf("addedAt" to FieldValue.serverTimestamp())).await()
    }

    suspend fun removeProviderFromFavorites(userId: String, providerId: String) {
        db.collection("users").document(userId)
            .collection("favoriteProviders").document(providerId)
            .delete().await()
    }
}
