package com.undef.manoslocales.ui.dataclasses

data class Product(
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val providerId: String,
    val createdAt: Long
)

