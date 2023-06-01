package com.example.weatherapp.service

import com.example.weatherapp.model.WeatherModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather?&units=metric&appid=0490490628edbc034c2e3cf724218a49")

    fun getData(
        @Query("q") cityName: String
    ): Single<WeatherModel>

}