package com.samruddhi.mvvmarchitecturelivedataretrofitpaging.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.samruddhi.mvvmarchitecturelivedataretrofitpaging.R
import com.samruddhi.mvvmarchitecturelivedataretrofitpaging.model.LocationModel
import com.samruddhi.mvvmarchitecturelivedataretrofitpaging.utils.AlertDialogManager
import com.samruddhi.mvvmarchitecturelivedataretrofitpaging.webservice.api.WebServiceApi

class MainRepository constructor(
    private val webServiceApi: WebServiceApi,
    private val context: Context
) : LiveData<LocationModel>() {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.also {
                    setLocationData(it)
                }
            }
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                setLocationData(location)
            }
        }
    }

    private fun setLocationData(location: Location) {
        value = LocationModel(
            longitude = location.longitude,
            latitude = location.latitude
        )
    }

    companion object {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 50000
            fastestInterval = 50000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: NetworkInfo? = null
        networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun displayNetworkLostDialog(context: Context) {
        val alertDialogManager = AlertDialogManager()
        alertDialogManager.createDialogWithOneBtn(
            context,
            "",
            context.resources.getString(R.string.noNetworkConnection),
            context.resources.getString(R.string.shared_btn_ok)
        ) { dialog, _ -> dialog.dismiss() }
    }

    fun getAllRestaurantsUsingLatLon(latitude: Double, longitude: Double, start: Int, count: Int) =
        webServiceApi.getAllRestaurantsUsingLatLon(
            latitude = latitude.toString(),
            longitude = longitude.toString(),
            start = start,
            count = count
        )

//    suspend fun getAllRestaurantsUsingLatLon() = webServiceApi.getAllRestaurantsUsingLatLon("12.86806863214614", "74.86873974116669")
}