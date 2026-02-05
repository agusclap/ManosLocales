package com.undef.manoslocales.ui.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://apis.datos.gob.ar/georef/api/"

    val instance: ManosLocalesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ManosLocalesApi::class.java)
    }
}
