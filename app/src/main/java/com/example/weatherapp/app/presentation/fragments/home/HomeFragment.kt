package com.example.weatherapp.app.presentation.fragments.home

import CityAdapter
import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
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
import com.example.weatherapp.app.presentation.fragments.kota.KotaFragment
import com.example.weatherapp.app.presentation.rv.WeatherForecastAdapter
import com.example.weatherapp.app.presentation.util.ConvertingManager
import com.example.weatherapp.app.presentation.util.ext.errorOccurred
import com.example.weatherapp.app.presentation.util.ext.loadingFinished
import com.example.weatherapp.app.presentation.util.ext.loadingStarted
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.databinding.StateLoadingBinding
import com.example.weatherapp.ext.setWeatherIcon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var _loadingBinding: StateLoadingBinding? = null
    private val loadingBinding: StateLoadingBinding get() = _loadingBinding!!

    @Inject
    lateinit var sp: SharedPreferences

    @Inject
    lateinit var repository: WeatherRepository



    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true || it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val cities = listOf(
            SingleCity("Jakarta"),
            SingleCity("Bekasi"),
            SingleCity("Bogor")
        )

        with(binding){
            this.recyclerView.adapter = CityAdapter(cities) { it ->
                val fragment = KotaFragment()
                fragment.setCity(it)
                getActivity()
                    ?.getSupportFragmentManager()
                    ?.beginTransaction()
                    ?.replace(R.id.nav_host_fragment , fragment)
                    ?.addToBackStack(null)
                    ?.commit()

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
        _binding = FragmentMainBinding.inflate(inflater, container, false)
       // _loadingBinding = StateLoadingBinding.bind(binding.root)

        requestLocationPermissions()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _loadingBinding = null
        _binding = null
    }
}
