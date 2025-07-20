package com.undef.manoslocales.ui.notifications

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.dataclasses.Product
import kotlinx.coroutines.tasks.await

class FavoritesRepository {

    private val db = FirebaseFirestore.getInstance()

    // --- PRODUCTOS ---

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
        try {
            val favoriteIdsSnapshot = db.collection("users").document(userId)
                .collection("favoriteProviders").get().await()
            val favoriteProviderIds = favoriteIdsSnapshot.documents.map { it.id }
            Log.d("FAV_REPO_DEBUG", "Buscando datos para ${favoriteProviderIds.size} proveedores favoritos.")
            if (favoriteProviderIds.isEmpty()) return emptyList()
            val providersSnapshot = db.collection("users")
                .whereIn("__name__", favoriteProviderIds).get().await()

            return providersSnapshot.documents.mapNotNull { doc ->
                try {
                    val user = doc.toObject(User::class.java)
                    user?.id = doc.id // Asignamos el ID del documento al objeto User
                    user
                } catch (e: Exception) {
                    null
                }
            }
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


    fun listenToNewProductsFromProviders(
        providerIds: List<String>,
        startTime: Long,
        onNewProduct: (Product) -> Unit
    ): ListenerRegistration {
        val productsRef = db.collection("products")

        return productsRef
            // Filtramos por los proveedores a los que el usuario está suscrito.
            .whereIn("providerId", providerIds)
            // Filtramos solo por productos creados DESPUÉS de que el "escucha" se inició.
            .whereGreaterThan("createdAt", startTime)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("NewProductListener", "Error en el listener:", error)
                    return@addSnapshotListener
                }

                // Iteramos sobre los cambios, buscando solo los de tipo AÑADIDO (nuevos).
                for (change in snapshots!!.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED) {
                        try {
                            val product = change.document.toObject(Product::class.java)
                            product.id = change.document.id
                            onNewProduct(product)
                        } catch (e: Exception) {
                            Log.e("NewProductListener", "Error al deserializar nuevo producto", e)
                        }
                    }
                }
            }
    }

    fun listenToProductChanges(productIds: List<String>, onProductUpdate: (Product) -> Unit): ListenerRegistration {
        val productsRef = db.collection("products")

        return productsRef.whereIn("__name__", productIds)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("PriceListenerRepo", "Error en el listener:", error)
                    return@addSnapshotListener
                }

                // Iteramos solo sobre los documentos que han sido MODIFICADOS.
                for (change in snapshots!!.documentChanges) {
                    if (change.type == DocumentChange.Type.MODIFIED) {
                        try {
                            val product = change.document.toObject(Product::class.java)
                            product.id = change.document.id
                            onProductUpdate(product)
                        } catch (e: Exception) {
                            Log.e("PriceListenerRepo", "Error al deserializar producto modificado", e)
                        }
                    }
                }
            }
    }
}


