package com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.databinding.ActivityMainBinding
import com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.model.RestaurantsDataList
import com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.utils.GpsLocationUtils
import com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.viewmodel.MainRepository
import com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.viewmodel.MainViewModel
import com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.viewmodel.MyViewModelFactory
import com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.webservice.api.WebApi

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101

class MainActivity : AppCompatActivity(), LocationListener {
    lateinit var viewModel: MainViewModel
    private val adapter = RestaurantsAdapter()
    lateinit var binding: ActivityMainBinding

    private var isGPSEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = binding.toolbarLayout.Toolbar
        toolbar.title = getString(R.string.app_name)

        GpsLocationUtils(this@MainActivity).turnGPSOn(object : GpsLocationUtils.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                this@MainActivity.isGPSEnabled = isGPSEnable
            }
        })
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        }
    }

    private fun invokeLocationAction() {
        when {
            isPermissionsGranted() -> startLocationUpdate()

            shouldShowRequestPermissionRationale() -> Toast.makeText(
                    this@MainActivity,
                    getString(R.string.permission_request),
                    Toast.LENGTH_LONG
            ).show()

            else -> ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_REQUEST
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        val retrofitService = WebApi.getAPIService(this@MainActivity)
        val mainRepository = MainRepository(retrofitService, this@MainActivity)
        binding.recyclerview.adapter = adapter

        viewModel = ViewModelProvider(
                this,
                MyViewModelFactory(mainRepository)
        ).get(MainViewModel::class.java)

        var latitude: Double = 0.0
        var longitude: Double = 0.0

        /*Location LiveData*/
//        viewModel.getLocationData().observe(this@MainActivity, {
//            Log.e("GPS", "GPS : " + getString(R.string.latLong, it.latitude, it.longitude))
//            latitude = it.latitude
//            longitude = it.longitude
//            viewModel.getAllRestaurantsNearBy(it.latitude, it.longitude)
//        })

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (location != null) {
            latitude = location.latitude
            longitude = location.longitude
            Log.e("GPS", "GPS : " + getString(R.string.latLong, latitude, longitude))
        }
        if (mainRepository.isNetworkConnected()) {
            if (latitude != 0.0 && longitude != 0.0) {
                viewModel.getAllRestaurantsNearBy(latitude, longitude)
            } else {
                viewModel.loading.value = false
                Toast.makeText(
                        this@MainActivity,
                        getString(R.string.permission_request),
                        Toast.LENGTH_LONG
                ).show()
            }
        } else {
            mainRepository.displayNetworkLostDialog(this@MainActivity)
        }

        viewModel.searchRestaurantsUsingLatLonRange.observe(this, {
            val restaurantsDataList: List<RestaurantsDataList> = it.restaurants
            adapter.setAllRestaurantsNearBy(restaurantsDataList, this@MainActivity)
        })

        viewModel.errorMessage.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                binding.progressDialog.visibility = View.VISIBLE
            } else {
                binding.progressDialog.visibility = View.GONE
            }
        })

    }

    private fun isPermissionsGranted() =
            ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
            ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }

    override fun onLocationChanged(location: Location) {

    }

    fun solution(A: IntArray): Int {
        val n: Int = A.size
        val present = BooleanArray(n + 1)

        for (i in 0 until n) {
            if (A[i] in 1..n) present[A[i]] = true
        }
        for (i in 1..n) if (!present[i]) return i
        return n + 1
    }

    fun solution(phone_numbers: Array<String>, phone_owners: Array<String>, number: String): String {
        // write your code in Kotlin 1.3.11 (Linux)
        val phoneNumbersSize: Int = phone_numbers.size
        val phoneOwners: Int = phone_owners.size

        val numberPresent = BooleanArray(phoneNumbersSize + 1)

        for (i in 0 until phoneNumbersSize) {
            if (number == phone_numbers[i]) {
                numberPresent[i] = true
            }
        }

        var name = ""

        for (i in 0..phoneOwners) {
            if (numberPresent[i]) {
                name = phone_owners[i]
            }
        }

        if (name == "") {
            name = number
        }

        return name
    }
}