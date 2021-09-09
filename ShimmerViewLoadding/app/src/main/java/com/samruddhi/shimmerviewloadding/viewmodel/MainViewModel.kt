package com.samruddhi.shimmerviewloadding.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.samruddhi.shimmerviewloadding.model.SearchRestaurantsUsingLatLonRange
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val searchRestaurantsUsingLatLonRange = MutableLiveData<SearchRestaurantsUsingLatLonRange>()
    val loading = MutableLiveData<Boolean>()

    fun getAllRestaurantsNearBy(latitude: Double, longitude: Double) {
        val response = mainRepository.getAllRestaurantsUsingLatLon(latitude, longitude)
        response.enqueue(object : Callback<SearchRestaurantsUsingLatLonRange> {
            override fun onResponse(
                call: Call<SearchRestaurantsUsingLatLonRange>,
                response: Response<SearchRestaurantsUsingLatLonRange>
            ) {
                searchRestaurantsUsingLatLonRange.postValue(response.body())
                loading.value = false
            }

            override fun onFailure(call: Call<SearchRestaurantsUsingLatLonRange>, t: Throwable) {
                onError("Error : ${(t.message)} ")
                loading.value = false
            }
        })
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

}