package com.undef.manoslocales.ui.network

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {
    // Estado para la lista de provincias
    var provincias by mutableStateOf<List<String>>(emptyList())
        private set

    // Estado para manejar la carga
    var isLoading by mutableStateOf(false)
        private set

    /**
     * Carga las provincias desde la API de GeoRef usando Retrofit.
     */
    fun loadProvincias() {
        if (provincias.isNotEmpty()) return // Evitar recargas innecesarias

        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getProvincias()
                // Convertimos la lista de objetos en una lista de nombres ordenados
                // Agregamos "Todas" al principio para los filtros
                provincias = listOf("Todas") + response.provincias
                    .map { it.nombre }
                    .sorted()
                
                Log.d("Retrofit", "Provincias cargadas exitosamente")
            } catch (e: Exception) {
                Log.e("Retrofit", "Error al cargar provincias: ${e.message}")
                // Fallback en caso de error (lista estática de emergencia)
                provincias = listOf(
                    "Todas", "Buenos Aires", "CABA", "Catamarca", "Chaco", "Chubut", "Córdoba", 
                    "Corrientes", "Entre Ríos", "Formosa", "Jujuy", "La Pampa", "La Rioja", 
                    "Mendoza", "Misiones", "Neuquén", "Río Negro", "Salta", "San Juan", 
                    "San Luis", "Santa Cruz", "Santa Fe", "Santiago del Estero", 
                    "Tierra del Fuego", "Tucumán"
                )
            } finally {
                isLoading = false
            }
        }
    }
}
