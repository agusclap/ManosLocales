package com.undef.manoslocales.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.manoslocales.ui.data.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    // Creamos una única instancia de nuestro gestor de preferencias.
    private val settingsManager = SettingsManager(application)

    // Exponemos los Flows del SettingsManager como StateFlows para que la UI de Compose
    // pueda observarlos fácilmente.
    val defaultCity = settingsManager.defaultCityFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val priceNotificationsEnabled = settingsManager.priceNotificationsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val newProductNotificationsEnabled = settingsManager.newProductNotificationsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    // --- Funciones que la UI llamará para guardar los cambios ---

    fun onDefaultCityChange(newCity: String) {
        viewModelScope.launch {
            settingsManager.setDefaultCity(newCity)
        }
    }

    fun onPriceNotificationsChange(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setPriceNotificationsEnabled(isEnabled)
        }
    }

    fun onNewProductNotificationsChange(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setNewProductNotificationsEnabled(isEnabled)
        }
    }
}