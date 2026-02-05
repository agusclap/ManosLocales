package com.undef.manoslocales.ui.network

/**
 * Modelos de datos para la API de GeoRef (Argentina)
 */
data class ProvinciasResponse(
    val provincias: List<ProvinciaApi>,
    val cantidad: Int,
    val total: Int
)

data class ProvinciaApi(
    val id: String,
    val nombre: String
)
