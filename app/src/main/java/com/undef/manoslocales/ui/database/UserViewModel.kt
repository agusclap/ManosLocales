package com.undef.manoslocales.ui.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.manoslocales.ui.data.SessionManager
import kotlinx.coroutines.launch


class UserViewModel (
    application: Application,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager // <--- Añade esto
) : AndroidViewModel(application) {

    fun userRegister(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = userRepository.registerUser(user)
            onResult(success)
        }
    }

    fun loginUser(email: String, passwordPlain: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.loginUser(email, passwordPlain)
            if (user != null) {
                sessionManager.saveLoginState(true, user.email) // Guarda el estado de login
            } else {
                sessionManager.saveLoginState(false) // Asegura que no esté logueado
            }
            onResult(user)
        }
    }

    // Nuevo: Función para cerrar sesión
    fun logoutUser() {
        sessionManager.logout()
    }

    // Nuevo: Función para verificar si el usuario ya está logueado
    fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    // Nuevo: Obtener email del usuario logueado
    fun getLoggedInUserEmail(): String? {
        return sessionManager.getLoggedInUserEmail()
    }
}