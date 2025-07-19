package com.undef.manoslocales.ui.dataclasses

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val providerId: String = "",
    val createdAt: Long = 0L,
    val category: String = ""
)



