package com.undef.manoslocales.ui.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        val DEFAULT_CITY_KEY = stringPreferencesKey("default_city")
        val PRICE_NOTIFICATIONS_KEY = booleanPreferencesKey("price_notifications_enabled")
        val NEW_PRODUCT_NOTIFICATIONS_KEY = booleanPreferencesKey("new_product_notifications_enabled")
    }

    // --- Flujos de Lectura (se mantienen igual) ---
    val defaultCityFlow: Flow<String> = context.dataStore.data.map { it[DEFAULT_CITY_KEY] ?: "" }
    val priceNotificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[PRICE_NOTIFICATIONS_KEY] ?: true }
    val newProductNotificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[NEW_PRODUCT_NOTIFICATIONS_KEY] ?: true }

    // --- Funciones de Escritura (con Logs) ---

    suspend fun setDefaultCity(city: String) {
        Log.d("SettingsManager", "Intentando guardar ciudad: $city")
        context.dataStore.edit { settings ->
            settings[DEFAULT_CITY_KEY] = city
        }
        Log.d("SettingsManager", "Ciudad guardada.")
    }

    suspend fun setPriceNotificationsEnabled(isEnabled: Boolean) {
        Log.d("SettingsManager", "Intentando guardar preferencia de PRECIOS: $isEnabled")
        context.dataStore.edit { settings ->
            settings[PRICE_NOTIFICATIONS_KEY] = isEnabled
        }
        Log.d("SettingsManager", "Preferencia de PRECIOS guardada.")
    }

    suspend fun setNewProductNotificationsEnabled(isEnabled: Boolean) {
        Log.d("SettingsManager", "Intentando guardar preferencia de NUEVOS PRODUCTOS: $isEnabled")
        context.dataStore.edit { settings ->
            settings[NEW_PRODUCT_NOTIFICATIONS_KEY] = isEnabled
        }
        Log.d("SettingsManager", "Preferencia de NUEVOS PRODUCTOS guardada.")
    }
}
