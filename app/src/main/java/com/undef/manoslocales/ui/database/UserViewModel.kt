// src/main/java/com/undef/manoslocales/ui/database/UserViewModel.kt
package com.undef.manoslocales.ui.database

import android.app.Application // Still need this if extending AndroidViewModel
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// UserViewModel now takes UserRepository as a parameter
class UserViewModel (application: Application, private val userRepository: UserRepository) : AndroidViewModel(application) {

    fun userRegister(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = userRepository.registerUser(user)
            onResult(success)
        }
    }

    // Example: For login, if you add a login function
    fun loginUser(email: String, passwordHash: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            // Perform password check here (ideally you'd hash the input password)
            if (user != null && user.password == passwordHash) {
                onResult(user)
            } else {
                onResult(null)
            }
        }
    }
}