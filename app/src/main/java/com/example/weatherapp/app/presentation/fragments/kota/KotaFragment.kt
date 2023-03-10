package com.example.weatherapp.app.presentation.fragments.kota

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.exceptions.LocationPermissionDeniedException
import com.example.domain.model.SingleCity
import com.example.domain.model.WeatherForecast
import com.example.domain.model.WeatherInfo
import com.example.domain.repository.WeatherRepository
import com.example.domain.state.State
import com.example.weatherapp.R
import com.example.weatherapp.app.MainActivity
import com.example.weatherapp.app.presentation.rv.WeatherForecastAdapter
import com.example.weatherapp.app.presentation.util.ConvertingManager
import com.example.weatherapp.app.presentation.util.ext.errorOccurred
import com.example.weatherapp.app.presentation.util.ext.loadingFinished
import com.example.weatherapp.app.presentation.util.ext.loadingStarted
import com.example.weatherapp.databinding.FragmentKotaBinding
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.databinding.StateLoadingBinding
import com.example.weatherapp.ext.setWeatherIcon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class KotaFragment : Fragment() {
    private var _binding: FragmentKotaBinding? = null
    private val binding get() = _binding!!

    private var _loadingBinding: StateLoadingBinding? = null
    private val loadingBinding: StateLoadingBinding get() = _loadingBinding!!

    @Inject
    lateinit var sp: SharedPreferences

    @Inject
    lateinit var repository: WeatherRepository

    private val viewModel: KotaViewModel by viewModels()
    private val adapter = WeatherForecastAdapter()

    var cityName = ""

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true || it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                Log.d("Permission Grant", cityName);
                viewModel.setKota(cityName);
                viewModel.loadData()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserves()
    }

    fun setCity(city: SingleCity) {
        cityName = city.name;
        Log.d("Info", city.name);
    }

    private fun initObserves() {
        viewModel.weatherForecast.observe(viewLifecycleOwner) { resWeatherForecast ->
            when (resWeatherForecast) {
                is State.Loading ->{
                    hideAll()
                    loadingBinding.loadingStarted()
                }
                is State.Success -> {
                    resWeatherForecast.data?.let {
                        showAll()
                        Log.d("tes", it.city.name)
                        loadingBinding.loadingFinished()
                        showWeatherForecast(it)
                        showActionBarTitle(it.city.name, it.city.country)
                        saveToPreferences(it.city.name, it.city.country)
                    }
                }
                is State.Error -> resWeatherForecast.error?.let { showError(it) }
            }
        }
    }

    private fun showError(it: Throwable) {
        loadingBinding.errorOccurred(it) {
            if (it is LocationPermissionDeniedException) {
                requestLocationPermissions()
            } else {
               // viewModel.loadData()
            }
        }
    }

    private fun requestLocationPermissions() {
        permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private fun showActionBarTitle(city: String, country: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title =
            getString(
                R.string.city_country_format,
                city,
                country
            )
    }


    private fun showAll() {
        for (view in binding.root.children) {
            view.visibility = View.VISIBLE
        }
    }

    private fun hideAll() {
        for (view in binding.root.children) {
            view.visibility = View.GONE
        }
    }

    private fun showWeatherForecast(
        weatherForecast: WeatherForecast,
    ) {
        var temperatureUnit: String
        var timeFormat: String

        PreferenceManager.getDefaultSharedPreferences(requireActivity()).run {
            temperatureUnit = getString(
                MainActivity.PREF_TEMPERATURE_UNIT_KEY,
                MainActivity.DEFAULT_TEMPERATURE_UNIT
            ).toString()
            timeFormat = getString(
                MainActivity.PREF_TIME_FORMAT_KEY,
                MainActivity.DEFAULT_TIMEFORMAT
            ).toString()
        }

        with(binding) {
            val converter = ConvertingManager(resources)
            weatherForecast.list[0].run {
                main.run {
                    tvTemperature.text = getString(
                        R.string.temperature_unit,
                        converter.convertTemp(temperatureUnit, temp),
                        temperatureUnit
                    )
                    tvVisibility.text = converter.convertVisibility(visibility)
                    tvHumidity.text = getString(R.string.humidity_unit, humidity)
                    tvFeelsLike.text =
                        getString(
                            R.string.feels_like,
                            converter.convertTemp(temperatureUnit, temp),
                            temperatureUnit
                        )
                }
                tvVisibility.text = converter.convertVisibility(visibility)
                tvWind.text = getString(R.string.wind_unit, converter.formatDouble(wind.speed))
                ivWeather.setWeatherIcon(weather[0].id, weather[0].icon)
            }
            weatherForecast.city.run {
                // EE, h:mmaa -> h:mmaa
                // EE, HH:mm -> HH:mm
                tvSunrise.text =
                    converter.convertTimeToString(timeFormat.replace("EE, ", ""), sunrise.toLong())
                tvSunset.text =
                    converter.convertTimeToString(timeFormat.replace("EE, ", ""), sunset.toLong())
            }
            textClock.format24Hour = timeFormat
//            setUpAdapter(timeFormat, temperatureUnit, weatherForecast.list.drop(1))
//            setUpRecycler()
        }
    }

    private fun setUpAdapter(
        timeFormat: String,
        temperatureUnit: String,
        list: List<WeatherInfo>
    ) {
        adapter.submitList(list)
        adapter.timeformat = timeFormat
        adapter.temperatureUnit = temperatureUnit
    }

    private fun setUpRecycler(){
        with(binding) {
            rvWeather.addItemDecoration(
                DividerItemDecoration(
                    context,
                    RecyclerView.HORIZONTAL
                )
            )
            rvWeather.setHasFixedSize(true)
            rvWeather.adapter = adapter
        }
    }

    private fun saveToPreferences(city: String, country: String) {
        sp.edit().run {
            putString(MainActivity.PREF_CITY_KEY, city)
            putString(MainActivity.PREF_COUNTRY_KEY, country)
            apply()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKotaBinding.inflate(inflater, container, false)
        _loadingBinding = StateLoadingBinding.bind(binding.root)

        requestLocationPermissions()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _loadingBinding = null
        _binding = null
    }
}
