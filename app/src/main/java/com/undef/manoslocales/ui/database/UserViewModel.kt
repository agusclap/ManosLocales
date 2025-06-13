// src/main/java/com.undef/manoslocales.ui.database/UserViewModel.kt
package com.undef.manoslocales.ui.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel (application: Application, private val userRepository: UserRepository) : AndroidViewModel(application) {

    fun userRegister(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = userRepository.registerUser(user)
            onResult(success)
        }
    }

    // NUEVA FUNCIÓN: Para iniciar sesión
    fun loginUser(email: String, passwordPlain: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.loginUser(email, passwordPlain)
            onResult(user)
        }
    }
}