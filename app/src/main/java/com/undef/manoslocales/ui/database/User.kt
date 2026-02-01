package com.undef.manoslocales.ui.database

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class User(
    @get:Exclude
    var id: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val phone: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val categoria: String? = null,
    val city: String? = null,
    val role: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    
    @get:PropertyName("isVerified")
    @set:PropertyName("isVerified")
    var isVerified: Boolean = false,

    val verificationCode: String = "",
    @get:Exclude val password: String = ""
)
