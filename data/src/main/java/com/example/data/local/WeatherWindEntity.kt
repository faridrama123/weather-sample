package com.example.data.local

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Main
import com.example.domain.model.Weather
import com.example.domain.model.Wind

@Entity(tableName = "weather_wind")
data class WeatherWindEntity (


    @PrimaryKey
    @NonNull
    var cityName : String,
    val speed: Double



)