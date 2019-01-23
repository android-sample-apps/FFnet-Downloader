package com.citymapper.codingchallenge.stoppoints

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.citymapper.codingchallenge.MainApplication
import com.citymapper.codingchallenge.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.location.LocationSettingsRequest
import com.nicolasmouchel.executordecorator.MutableDecorator
import kotlinx.android.synthetic.main.activity_maps.*
import javax.inject.Inject


class StopPointsActivity : AppCompatActivity(), StopPointsView, StopPointListener {

    @Inject lateinit var controller: StopPointsController
    @Inject lateinit var view: MutableDecorator<StopPointsView>

    private lateinit var adapter: StopPointsAdapter
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    companion object {
        private const val UPDATE_INTERVAL = 10 * 1000
        private const val FASTEST_INTERVAL = 2000
        private const val REQUEST_FINE_LOCATION = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        MainApplication.getComponent(this).plus(StopPointsModule()).inject(this)


        view.mutate(this)
        adapter = StopPointsAdapter(emptyList(), this)
        stopPointsRecyclerView.layoutManager = LinearLayoutManager(this)
        stopPointsRecyclerView.adapter = adapter

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        checkPermissions()
        if (ContextCompat.checkSelfPermission(this,
                                              android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                controller.loadStopPoints(location)
            }
        }

        startLocationUpdates()
    }

    fun startLocationUpdates() {
        // Create the location request to start receiving updates
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = UPDATE_INTERVAL.toLong()
        locationRequest.fastestInterval = FASTEST_INTERVAL.toLong()

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(this,
                                              android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            getFusedLocationProviderClient(this).requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        // do work here
                        onLocationChanged(locationResult!!.lastLocation)
                    }
                },
                Looper.myLooper())
        }
    }

    fun onLocationChanged(location: Location) {
        Toast.makeText(this, "Location Changed to " + location.latitude + ":" + location.longitude, Toast.LENGTH_LONG)
                .show()
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            requestPermissions()
            return false
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_FINE_LOCATION
        )
    }

    override fun onDestroy() {
        view.mutate(null)
        super.onDestroy()
    }

    override fun displayStopPoints(stopPoints: List<StopPoint>) {
        adapter.updateData(stopPoints)
        Handler().postDelayed({
                                  controller.loadArrivalTimes()
                              }, 1000)
    }

    override fun onStopPointClicked(stopPoint: StopPoint) {
        Toast.makeText(this, "Clicked on StopPoint #" + stopPoint.id, Toast.LENGTH_LONG).show()
    }
}
