package com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.webservice.api

import com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.model.SearchRestaurantsUsingLatLonRange
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WebServiceApi {
    @GET("search")
    suspend fun getAllRestaurantsUsingLatLon(@Query("lat") latitude: String, @Query("lon") longitude: String) : Response<SearchRestaurantsUsingLatLonRange>
}