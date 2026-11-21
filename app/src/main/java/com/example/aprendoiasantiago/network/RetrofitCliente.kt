package com.example.aprendoiasantiago.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Emulador Android Studio: "http://10.0.2.2:8080/"
    // Celular físico por Wi-Fi: reemplaza por la IPv4 de tu PC (ej. "http://192.168.1.50:8080/")
    private const val BACKEND_URL = "http://10.0.2.2:8080/"

    val backendApi: BackendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApiService::class.java)
    }

    // Cliente independiente para la API Externa (IE 3.1.4)
    val weatherApi: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}