package com.example.weatherapp.app.presentation.fragments.kota

import android.app.Application
import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.exceptions.InvalidCityException
import com.example.domain.exceptions.LocationRequestFailedException
import com.example.domain.model.WeatherForecast
import com.example.domain.repository.WeatherRepository
import com.example.domain.state.State
import com.example.weatherapp.app.MainActivity
import com.example.weatherapp.app.presentation.util.LocationPermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KotaViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val sp: SharedPreferences,
    application: Application
) : AndroidViewModel(application) {

    private val _weatherForecast: MutableLiveData<State<WeatherForecast>> =
        MutableLiveData(State.Loading())
    val weatherForecast: LiveData<State<WeatherForecast>> = _weatherForecast

    private val locationPermissionManager = LocationPermissionManager(application)

    init {
       // loadData()
    }

    private var kota = "";
    fun setKota(value : String){
        kota = value;
    }


    fun loadData() = viewModelScope.launch {
        _weatherForecast.value = State.Loading()

            val city = kota;
            if (!city.isNullOrEmpty() && city.isNotBlank()) {
                Log.d("Kota Val",city.toString() );
                _weatherForecast.value = repository.getWeatherForecast(city)
            } else {
                Log.d("Kota Val",city.toString() );
                _weatherForecast.value = State.Error(InvalidCityException())
            }

    }

    private fun loadDataAuto() {
        val locationRes = locationPermissionManager.getLocation()
        locationRes.fold(
            onSuccess = { task ->
                task.addOnSuccessListener {
                    if (it == null) {
                        _weatherForecast.value = State.Error(LocationRequestFailedException())
                    } else {
                        updateWeatherForecastAuto(it)
                    }
                }
            },
            onFailure = {
                _weatherForecast.value = State.Error(it)
            })
    }

    private fun updateWeatherForecastAuto(it: Location) =
        viewModelScope.launch {
            _weatherForecast.value = repository.getWeatherForecast(it.latitude, it.longitude)
        }

}
