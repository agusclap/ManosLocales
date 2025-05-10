package com.undef.manoslocales.ui.theme

data class Emprendedor(
    val id: Int,
    val nombre: String,
    val ubicacion: String,
    val categoria: String,
    val imagenUrl: String
)

data class Proveedor(
    val id: Int,
    val nombre: String,
    val ubicacion: String,
    val categoria: String,
    val imagenUrl: String,
    val favorito: Boolean = false
)