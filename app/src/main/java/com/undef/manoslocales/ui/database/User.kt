package com.undef.manoslocales.ui.database

import com.google.firebase.firestore.Exclude // Importante para la seguridad

/**
 * Data class para representar un Usuario.
 *
 * MODIFICACIÓN: Se han añadido valores por defecto a todas las propiedades.
 * Esto es un requisito técnico de Firestore para poder convertir los documentos
 * de la base de datos en objetos Kotlin.
 *
 * Los datos reales de tu base de datos NO se verán afectados ni se pondrán en blanco.
 */
data class User(
    val nombre: String = "",
    val apellido: String = "",
    val phone: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val categoria: String? = null, // Este ya estaba bien, null es un valor por defecto válido
    val city: String? = null,      // Este también estaba bien
    val role: String = "",
    val lat: Double? = null,
    val lng: Double? = null,

    // ¡ADVERTENCIA DE SEGURIDAD!
    // El campo 'password' se ha excluido para que NUNCA se guarde en la base de datos de Firestore.
    // Guardar contraseñas en texto plano es un riesgo de seguridad muy grande.
    // La contraseña solo debe usarse para el proceso de registro/login con Firebase Authentication.
    @get:Exclude val password: String = ""
)