package com.citymapper.codingchallenge.stoppoints

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.citymapper.codingchallenge.MainApplication
import com.citymapper.codingchallenge.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nicolasmouchel.executordecorator.MutableDecorator
import kotlinx.android.synthetic.main.activity_maps.*
import javax.inject.Inject
import android.location.LocationManager



class StopPointsActivity : AppCompatActivity(), StopPointsView, StopPointListener {

    @Inject lateinit var controller: StopPointsController
    @Inject lateinit var view: MutableDecorator<StopPointsView>

    private lateinit var adapter: StopPointsAdapter
    private lateinit var locationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        MainApplication.getComponent(this).plus(StopPointsModule()).inject(this)

        locationClient = LocationServices.getFusedLocationProviderClient(this)

        view.mutate(this)
        adapter = StopPointsAdapter(emptyList(), this)
        stopPointsRecyclerView.layoutManager = LinearLayoutManager(this)
        stopPointsRecyclerView.adapter = adapter

        locationClient.lastLocation.addOnSuccessListener { location: Location? ->
            controller.loadStopPoints(location)
        }
    }

    override fun onDestroy() {
        view.mutate(null)
        super.onDestroy()
    }

    override fun displayStopPoints(stopPoints: List<StopPoint>) {
        adapter.updateData(stopPoints)
    }

    override fun onStopPointClicked(stopPoint: StopPoint) {
        Toast.makeText(this, "Clicked on StopPoint #" + stopPoint.id, Toast.LENGTH_LONG).show()
    }
}
