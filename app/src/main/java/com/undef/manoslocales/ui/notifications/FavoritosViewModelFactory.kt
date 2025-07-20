package com.undef.manoslocales.ui.notifications

import com.undef.manoslocales.ui.navigation.FavoritosViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.undef.manoslocales.ui.data.SessionManager
import com.undef.manoslocales.ui.notifications.FavoritesRepository

/**
 * Fábrica para crear instancias de FavoritosViewModel.
 *
 * Es OBLIGATORIA porque nuestro FavoritosViewModel ahora tiene un constructor
 * con parámetros (dependencias), y el sistema Android necesita saber cómo
 * crear una instancia de este tipo.
 *
 * @param repository La instancia del repositorio que se comunicará con Firestore.
 * @param sessionManager La instancia del gestor de sesión que sabe quién está logueado.
 */
class FavoritosViewModelFactory(
    private val repository: FavoritesRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    /**
     * Este es el método que Android llama cuando le pedís un ViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verificamos si la clase que nos piden crear es, de hecho, FavoritosViewModel.
        if (modelClass.isAssignableFrom(FavoritosViewModel::class.java)) {
            // Si lo es, creamos la instancia pasándole las "piezas" (dependencias)
            // que recibimos en el constructor de la fábrica.
            @Suppress("UNCHECKED_CAST")
            return FavoritosViewModel(repository, sessionManager) as T
        }
        // Si por alguna razón nos piden crear un ViewModel que no conocemos, lanzamos un error.
        throw IllegalArgumentException("Clase de ViewModel desconocida: ${modelClass.name}")
    }
}
