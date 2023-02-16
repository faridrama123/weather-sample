package com.example.data.local

import androidx.room.*


@Dao
interface KotaDao {

    @Query("SELECT * FROM city where name = :name")
    fun getCity(name: String): CityEntity

    @Query("SELECT * FROM weather_info where cityName = :name")
    fun getWeatherInfo(name: String): WeatherInfoEntity

    @Query("SELECT * FROM weather_main where cityName = :name")
    fun getWeatherMain(name: String): WeatherMainEntity

    @Query("SELECT * FROM weather_weather where cityName = :name")
    fun getWeatherWeather(name: String): WeatherWeatherEntity

    @Query("SELECT * FROM weather_wind where cityName = :name")
    fun getWeatherWind(name: String): WeatherWindEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: CityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherInfo(weatherInfoEntity: WeatherInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherMain(weatherMainEntity: WeatherMainEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherWeather(weatherWeatherEntity: WeatherWeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherWind(weatherWindEntity: WeatherWindEntity)


}