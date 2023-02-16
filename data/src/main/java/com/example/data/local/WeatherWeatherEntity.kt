package com.example.data.local

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Main
import com.example.domain.model.Weather
import com.example.domain.model.Wind

@Entity(tableName = "weather_weather")
data class WeatherWeatherEntity (


    @PrimaryKey
    @NonNull
    var cityName : String,
    val description: String,
    val icon: String,
    val id: Int



)