package com.samruddhi.shimmerviewloadding

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.samruddhi.shimmerviewloadding.model.RestaurantsDataList
import com.samruddhi.shimmerviewloadding.utils.GpsLocationUtils
import com.samruddhi.shimmerviewloadding.viewmodel.MainRepository
import com.samruddhi.shimmerviewloadding.viewmodel.MainViewModel
import com.samruddhi.shimmerviewloadding.viewmodel.MyViewModelFactory
import com.samruddhi.shimmerviewloadding.webservice.api.WebApi
import java.util.ArrayList

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101

class MainActivity : AppCompatActivity(), LocationListener {

    private val TAG = "MainActivity"

    private lateinit var viewModel: MainViewModel
    private var isGPSEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.Toolbar)
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

        val shimmerLayout: ShimmerFrameLayout = findViewById(R.id.shimmerLayout)
        val recyclerview: RecyclerView = findViewById(R.id.recyclerview)

        val mainRepository = MainRepository(retrofitService, this@MainActivity)

        viewModel = ViewModelProvider(
                this,
                MyViewModelFactory(mainRepository)
        ).get(MainViewModel::class.java)

        var latitude: Double = 0.0
        var longitude: Double = 0.0

        /*Location LiveData*/
//        viewModel.getLocationData().observe(this@MainActivity, {
//            Log.e("GPS", "GPS : " + getString(R.string.latLong, it.latitude, it.longitude))
//
//            if (mainRepository.isNetworkConnected()) {
//                if (it.latitude != 0.0 && it.longitude != 0.0) {
//                    viewModel.getAllRestaurantsNearBy(it.latitude, it.longitude)
//                } else {
//                    viewModel.loading.value = false
//                    Toast.makeText(
//                        this@MainActivity,
//                        getString(R.string.permission_request),
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            } else {
//                mainRepository.displayNetworkLostDialog(this@MainActivity)
//            }
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
            val adapter = RestaurantsAdapter()
            recyclerview.adapter = adapter
            adapter.setAllRestaurantsNearBy(restaurantsDataList, this@MainActivity)
        })

        viewModel.errorMessage.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                shimmerLayout.startShimmer();
                shimmerLayout.visibility = View.VISIBLE;
            } else {
                shimmerLayout.stopShimmer();
                shimmerLayout.visibility = View.GONE;
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
}