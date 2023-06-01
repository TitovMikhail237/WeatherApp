package com.example.weatherapp.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var get: SharedPreferences
    private lateinit var set: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        get = getSharedPreferences(packageName, MODE_PRIVATE)
        set = get.edit()

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val cName = get.getString("cityName", "london")
        binding.edtCityName.setText(cName)

        viewModel.refreshData(cName!!)

        getLiveData()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.llDataView.visibility = View.GONE
            binding.tvError.visibility = View.GONE
            binding.pbLoading.visibility = View.GONE

            var cityName = get.getString("cityName", cName)
            binding.edtCityName.setText(cityName)
            viewModel.refreshData(cityName!!)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        binding.emgSearchCityName.setOnClickListener {
            val cityName = binding.edtCityName.text.toString()
            set.putString("cityName", cityName)
            set.apply()
            viewModel.refreshData(cityName)

        }
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    private fun getLiveData() {
        viewModel.weatherData.observe(this, Observer { data ->
            data?.let {
                binding.llDataView.visibility = View.VISIBLE
                binding.tvDegree.text = data.main.temp.toString()+"°C"
                binding.tvCountryCode.text = data.sys.country
                binding.tvCityName.text = data.name
                binding.humidity.text = data.main.humidity.toString()
                binding.tvSpeed.text = data.wind.speed.toString()+"%"
                binding.tvLat.text = data.coord.lat.toString()
                binding.tvLon.text = data.coord.lon.toString()

                Glide.with(this)
                    .load("http://openweathermap.org/img/wn/" + data.weather.get(0).icon +"@2x.png")
                    .into(binding.imgWeatherIcon)
            }
        })

        viewModel.weatherLoad.observe(this, Observer { load ->
            load?.let {
                if (load) {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                    binding.llDataView.visibility = View.GONE
                } else {
                    binding.pbLoading.visibility = View.GONE
                }
            }
        })

        viewModel.weatherError.observe(this, Observer { error ->
            error?.let {
                if (error) {
                    binding.tvError.visibility = View.VISIBLE
                    binding.pbLoading.visibility = View.GONE
                    binding.llDataView.visibility = View.GONE
                } else {
                    binding.tvError.visibility = View.GONE
                }
            }
        })
    }
}