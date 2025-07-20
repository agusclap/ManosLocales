package com.undef.manoslocales.ui.database

data class User(
    val nombre: String,
    val apellido: String,
    val phone: String,
    val email: String,
    val password: String,
    val profileImageUrl: String,
    val categoria: String? = null,
    val city: String? = null,
    val role: String
)
