package com.samruddhi.shimmerviewloadding.webservice.api

import com.samruddhi.shimmerviewloadding.model.SearchRestaurantsUsingLatLonRange
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WebServiceApi {
    @GET("search")
    fun getAllRestaurantsUsingLatLon(@Query("lat") latitude: String, @Query("lon") longitude: String) : Call<SearchRestaurantsUsingLatLonRange>
}