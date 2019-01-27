package com.citymapper.codingchallenge.stoppoints

import android.location.Location
import android.os.Handler

interface StopPointsController {
    fun loadStopPoints(location: Location?)
    fun startArrivalTimesLoading()
    fun cancelArrivalTimesLoading()
}

class StopPointsControllerImpl(
    private val interactor: StopPointsInteractor,
    private val handler: Handler
) : StopPointsController {

    companion object {
        private const val DEFAULT_LAT = 51.510
        private const val DEFAULT_LON = -0.09
        private const val MAX_METERS = 50000
        private const val REFRESH_TIME = 30000L
    }

    private val runnable = Runnable {
        startArrivalTimesLoading()
    }

    override fun loadStopPoints(location: Location?) {
        location?.let {
            val distanceToLondon = it.distanceTo(londonLocation())
            if (distanceToLondon < MAX_METERS) {
                interactor.loadStopPoints(it.latitude, it.longitude)
            } else {
                interactor.loadStopPoints(DEFAULT_LAT, DEFAULT_LON)
            }
        } ?: interactor.loadStopPoints(DEFAULT_LAT, DEFAULT_LON)
    }

    override fun startArrivalTimesLoading() {
        interactor.loadArrivalTimes()
        handler.postDelayed(runnable, REFRESH_TIME)
    }

    override fun cancelArrivalTimesLoading() {
        handler.removeCallbacks(runnable)
    }

    private fun londonLocation(): Location = Location("LONDON").apply {
        latitude = DEFAULT_LAT
        longitude = DEFAULT_LON
    }
}
