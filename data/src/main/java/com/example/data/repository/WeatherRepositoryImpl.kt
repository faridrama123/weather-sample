package com.example.data.repository


import android.util.Log
import com.example.data.Api
import com.example.data.local.*
import com.example.domain.model.*

import com.example.domain.repository.WeatherRepository
import com.example.domain.state.State
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(private val api: Api, private val dao : KotaDao) :
    WeatherRepository {

    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double
    ): State<WeatherForecast> =
        try {
            State.Success(api.getWeatherForecast(latitude, longitude))
        } catch (e: IOException) {
            State.Error(e)
        } catch (e: HttpException) {
            State.Error(e)
        }

    override suspend fun getWeatherForecast(cityName: String): State<WeatherForecast> =
        try {
            val data : WeatherForecast = api.getWeatherForecast(cityName);
            val mapCity = CityEntity (data.city.id, data.city.country, cityName, data.city.sunrise, data.city.sunset );
            val mapWeatherInfo = WeatherInfoEntity (cityName, data.list[0].dt, data.list[0].visibility);
            val mapWeatherMain = WeatherMainEntity (cityName, data.list[0].main.feels_like, data.list[0].main.humidity, data.list[0].main.temp);
            val mapWeatherWeather = WeatherWeatherEntity (cityName, data.list[0].weather[0].description, data.list[0].weather[0].icon, data.list[0].weather[0].id  )
            val mapWeatherWind = WeatherWindEntity (cityName, data.list[0].wind.speed)

            dao.insert(mapCity);
            dao.insertWeatherWeather(mapWeatherWeather);
            dao.insertWeatherInfo(mapWeatherInfo);
            dao.insertWeatherMain(mapWeatherMain);
            dao.insertWeatherWind(mapWeatherWind);

            val city = dao.getCity(cityName);
            Log.d("City Of", city.toString());
            State.Success(data)
        } catch (e: IOException) {
            try {
                val city = dao.getCity(cityName);
                val dWWeather = dao.getWeatherWeather(cityName)
                val dWWind = dao.getWeatherWind(cityName)
                val dWMain = dao.getWeatherMain(cityName)
                val dWInfo = dao.getWeatherInfo(cityName)

                if(city != null && dWWeather != null && dWWind != null && dWMain != null && dWInfo != null){

                    val mapCity = City(city.name, city.id, city.name, city.sunrise, city.sunset)

                    val wWeather = Weather(dWWeather.description, dWWeather.icon, dWWeather.id);

                    val wWind = Wind(dWWind.speed,);

                    val wMain = Main(dWMain.feels_like, dWMain.humidity, dWMain.temp);

                    val wWinfo = WeatherInfo(dWInfo.dt,wMain , dWInfo.visibility, listOf(wWeather),  wWind);

                    val listweather: List<WeatherInfo> = listOf(wWinfo)

                    val data = WeatherForecast (mapCity, listweather)

                    State.Success(data)
                }else{
                    State.Error(e)
                }
            } catch (e: IOException){
                State.Error(e)
            }

        } catch (e: HttpException) {
            State.Error(e)
        }
}
