package com.undef.manoslocales.ui.dataclasses

import com.google.firebase.firestore.Exclude

/**
 * Data class para representar un Producto.
 *
 * MODIFICACIÓN:
 * 1. Se han añadido valores por defecto a todas las propiedades para que sea
 * compatible con la deserialización de Firestore.
 * 2. El campo 'id' se ha cambiado a 'var' para que podamos asignarle el
 * ID del documento de Firestore después de crear el objeto.
 * 3. Se ha añadido @get:Exclude al campo 'id' para que Firestore no intente
 * guardarlo como un campo dentro del documento.
 */
data class Product(
    @get:Exclude
    var id: String = "",

    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val providerId: String = "",
    val createdAt: Long = 0L,
    val category: String = "",
    val city: String = ""
)
