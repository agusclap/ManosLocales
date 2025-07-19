package com.undef.manoslocales.ui.database

import androidx.compose.ui.semantics.Role
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val phone: String,
    val email: String,
    val password: String,
    val profileImageUrl: String,
    val categoria: String? = null,         // categoría asignada si es proveedor
    val role: String
    )