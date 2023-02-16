package com.example.domain.repository

import com.example.domain.model.WeatherForecast
import com.example.domain.state.State

interface WeatherRepository {

    suspend fun getWeatherForecast(latitude: Double, longitude: Double): State<WeatherForecast>

    suspend fun getWeatherForecast(cityName: String): State<WeatherForecast>

}
