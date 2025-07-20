package com.undef.manoslocales.ui.notifications

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.undef.manoslocales.ui.data.SessionManager
import com.undef.manoslocales.ui.navigation.FavoritosViewModel
import com.undef.manoslocales.ui.notifications.FavoritesRepository

/**
 * Fábrica para crear instancias de FavoritosViewModel.
 * Es necesaria porque el ViewModel ahora necesita 'Application', 'Repository' y 'SessionManager'
 * para ser construido.
 */
class FavoritosViewModelFactory(
    private val application: Application,
    private val repository: FavoritesRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritosViewModel(application, repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

