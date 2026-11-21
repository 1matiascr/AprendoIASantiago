package com.example.aprendoiasantiago.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
        @SerializedName("current_weather")
        val currentWeather: CurrentWeather
)

data class CurrentWeather(
        val temperature: Double,
        val windspeed: Double,
        val weathercode: Int
)

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getSantiagoWeather(
            @Query("latitude") latitude: Double = -33.4489,
            @Query("longitude") longitude: Double = -70.6693,
            @Query("current_weather") currentWeather: Boolean = true
    ): WeatherResponse
}