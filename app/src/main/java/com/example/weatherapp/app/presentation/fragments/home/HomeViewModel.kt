package com.example.weatherapp.app.presentation.fragments.home

import android.app.Application
import android.content.SharedPreferences
import android.location.Location
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
class HomeViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val sp: SharedPreferences,
    application: Application
) : AndroidViewModel(application) {



}
