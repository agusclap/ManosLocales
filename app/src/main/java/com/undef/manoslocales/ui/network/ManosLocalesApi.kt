package com.undef.manoslocales.ui.network

import retrofit2.http.GET

interface ManosLocalesApi {
    /**
     * Obtiene la lista de provincias de la API oficial de Argentina (GeoRef).
     * El parámetro campos=id,nombre nos permite recibir solo lo que necesitamos.
     */
    @GET("provincias?campos=id,nombre")
    suspend fun getProvincias(): ProvinciasResponse
}
