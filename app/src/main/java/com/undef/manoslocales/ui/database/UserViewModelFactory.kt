package com.undef.manoslocales.ui.database

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.undef.manoslocales.ui.data.SessionManager

class UserViewModelFactory(
    private val application: Application,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(application, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
