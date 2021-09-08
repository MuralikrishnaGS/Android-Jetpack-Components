package com.samruddhi.mvvmarchitecturelivedataretrofitpaging.webservice.api

import com.samruddhi.mvvmarchitecturelivedataretrofitpaging.model.SearchRestaurantsUsingLatLonRange
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WebServiceApi {
    @GET("search")
    fun getAllRestaurantsUsingLatLon(@Query("lat") latitude: String, @Query("lon") longitude: String, @Query("start") start: Int, @Query("count") count: Int) : Call<SearchRestaurantsUsingLatLonRange>
}