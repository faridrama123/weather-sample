package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities =
[
    CityEntity::class,
    WeatherInfoEntity::class,
    WeatherMainEntity::class,
    WeatherWeatherEntity::class,
    WeatherWindEntity::class,
], version = 8, exportSchema = false)
abstract  class TeamDatabase : RoomDatabase() {
    abstract fun kotaDao() : KotaDao
}