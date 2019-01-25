package com.citymapper.codingchallenge.stoppoints

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.citymapper.codingchallenge.MainApplication
import com.citymapper.codingchallenge.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nicolasmouchel.executordecorator.MutableDecorator
import kotlinx.android.synthetic.main.activity_maps.*
import javax.inject.Inject

class StopPointsActivity : AppCompatActivity(), StopPointsView, StopPointListener {

    @Inject lateinit var controller: StopPointsController
    @Inject lateinit var view: MutableDecorator<StopPointsView>

    private lateinit var adapter: StopPointsAdapter
    private lateinit var locationClient: FusedLocationProviderClient

    companion object {
        private const val REQUEST_FINE_LOCATION = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        MainApplication.getComponent(this).plus(StopPointsModule()).inject(this)

        view.mutate(this)
        adapter = StopPointsAdapter(emptyList(), this)
        stopPointsRecyclerView.layoutManager = LinearLayoutManager(
            this
        )
        stopPointsRecyclerView.adapter = adapter

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkPermissions()) {
            loadLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                loadLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location is needed, but was not granted",
                    Toast.LENGTH_LONG
                ).show()
            }
            return
        }
    }

    override fun onDestroy() {
        view.mutate(null)
        super.onDestroy()
    }

    override fun displayStopPoints(stopPoints: List<StopPointModel>) {
        adapter.updateData(stopPoints)
    }

    override fun onStopPointClicked(stopPoint: StopPointModel) {
        Toast.makeText(this, "Clicked on StopPoint #" + stopPoint.id, Toast.LENGTH_LONG).show()
    }

    private fun loadLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                controller.loadStopPoints(location)
                controller.loadArrivalTimes()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestPermissions()
            false
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_FINE_LOCATION
        )
    }
}
