package com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.model.SearchRestaurantsUsingLatLonRange
import kotlinx.coroutines.*

class MainViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val searchRestaurantsUsingLatLonRange = MutableLiveData<SearchRestaurantsUsingLatLonRange>()
    val loading = MutableLiveData<Boolean>()
    var job: Job? = null

    fun getLocationData() = mainRepository

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun getAllRestaurantsNearBy(latitude:Double, longitude: Double) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).async {
            val response = mainRepository.getAllRestaurantsUsingLatLon(latitude, longitude)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    searchRestaurantsUsingLatLonRange.postValue(response.body())
                    loading.value = false
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }

//        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            val response = mainRepository.getAllRestaurantsUsingLatLon()
//            withContext(Dispatchers.Main) {
//                if (response.isSuccessful) {
//                    searchRestaurantsUsingLatLonRange.postValue(response.body())
//                    loading.value = false
//                } else {
//                    onError("Error : ${response.message()} ")
//                }
//            }
//        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}