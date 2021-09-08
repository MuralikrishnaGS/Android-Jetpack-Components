package com.samruddhi.mvvmarchitecturelivedataretrofitpaging

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.samruddhi.mvvmarchitecturelivedataretrofitpaging.model.RestaurantsDataList
import com.samruddhi.mvvmarchitecturelivedataretrofitpaging.utils.GpsLocationUtils
import com.samruddhi.mvvmarchitecturelivedataretrofitpaging.viewmodel.MainRepository
import com.samruddhi.mvvmarchitecturelivedataretrofitpaging.viewmodel.MainViewModel
import com.samruddhi.mvvmarchitecturelivedataretrofitpaging.viewmodel.MyViewModelFactory
import com.samruddhi.mvvmarchitecturelivedataretrofitpaging.webservice.api.WebApi
import java.util.*

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101

class MainActivity : AppCompatActivity(), LocationListener {

    private val TAG = "MainActivity"

    private lateinit var viewModel: MainViewModel
    private lateinit var mainRepository: MainRepository
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val restaurantsList: ArrayList<RestaurantsDataList> = ArrayList<RestaurantsDataList>()

    private var location: Location? = null
    private var isGPSEnabled = false

    private val FIRST_PAGE = 1
    private val ITEMS_PER_PAGE = 10

    private var isScrolling = false
    private var lazyLoadPageNumber = 1
    private var totalPages = 0

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

        val progressDialog: ProgressBar = findViewById(R.id.progressDialog)
        val recyclerview: RecyclerView = findViewById(R.id.recyclerview)
        val linearLayoutLoadMore: LinearLayout = findViewById(R.id.linear_layout_load_more)

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        mainRepository = MainRepository(retrofitService, this@MainActivity)

        viewModel = ViewModelProvider(
            this,
            MyViewModelFactory(mainRepository)
        ).get(MainViewModel::class.java)

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
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        swipeRefreshLayout.setOnRefreshListener { getNewDataFromApi(FIRST_PAGE) }

        getNewData()

        viewModel.searchRestaurantsUsingLatLonRange.observe(this, {
            val linearLayoutManager = LinearLayoutManager(this)
            val recyclerViewState = recyclerview.layoutManager?.onSaveInstanceState()
            val restaurantsDataList: List<RestaurantsDataList> = it.restaurants
            restaurantsList.addAll(restaurantsDataList)
            val restaurantsAdapter = RestaurantsAdapter()
            restaurantsAdapter.setAllRestaurantsNearBy(restaurantsList, this@MainActivity)
            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = restaurantsAdapter
            recyclerview.layoutManager?.onRestoreInstanceState(recyclerViewState)

            recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrolling = true
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val currentItems: Int = linearLayoutManager.childCount
                    val totalItems: Int = linearLayoutManager.itemCount
                    val scrolledOutItems: Int = linearLayoutManager.findFirstVisibleItemPosition()
                    Log.d(TAG, "onScrolled: scrolledOutItems: $scrolledOutItems")
                    Log.d(TAG, "onScrolled: totalItems: $totalItems")
                    Log.d(TAG, "onScrolled: currentItems: $currentItems")
                    if (totalPages > 0) {
                        if (lazyLoadPageNumber <= totalPages) {
                            if (isScrolling && totalItems == currentItems + scrolledOutItems) {
                                // Load new data
                                linearLayoutLoadMore.visibility = View.VISIBLE
                                lazyLoadPageNumber++
                                AsyncTask.execute { getNewDataFromApi(lazyLoadPageNumber) }
                            }
                        } else {
                            linearLayoutLoadMore.visibility = View.GONE
                        }
                    }
                }
            })

            totalPages = it.results_found

            runOnUiThread {
                linearLayoutLoadMore.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }

        })

        viewModel.errorMessage.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                progressDialog.visibility = View.VISIBLE
            } else {
                progressDialog.visibility = View.GONE
            }
        })

    }

    private fun getNewData() {
        AsyncTask.execute { getNewDataFromApi(FIRST_PAGE) }
    }

    private fun getNewDataFromApi(pageNumber: Int) {
        var latitude = 0.0
        var longitude = 0.0

        if (1 == pageNumber) {
            restaurantsList.clear()
            lazyLoadPageNumber = 1
        }

        if (location != null) {
            latitude = location!!.latitude
            longitude = location!!.longitude
        }
        if (mainRepository.isNetworkConnected()) {
            if (latitude != 0.0 && longitude != 0.0) {
                viewModel.getAllRestaurantsNearBy(latitude, longitude, pageNumber, ITEMS_PER_PAGE)
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