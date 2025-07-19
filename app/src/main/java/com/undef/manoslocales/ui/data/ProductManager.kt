package com.undef.manoslocales.ui.data

import com.google.firebase.firestore.FirebaseFirestore
import com.undef.manoslocales.ui.dataclasses.Product

class ProductManager {
    private val firestore = FirebaseFirestore.getInstance()

    fun createProduct(product: Product, onResult: (Boolean, String?) -> Unit) {
        firestore.collection("products")
            .add(product)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}
