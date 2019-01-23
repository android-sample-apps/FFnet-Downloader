package com.citymapper.codingchallenge.stoppoints

import android.location.Location

interface StopPointsController {
    fun loadStopPoints(location: Location?)
    fun loadArrivalTimes()
}

class StopPointsControllerImpl(private val interactor: StopPointsInteractor) : StopPointsController {

    companion object {
        private const val DEFAULT_LAT = 51.512
        private const val DEFAULT_LON = -0.127
        private const val MAX_METERS = 50000
    }

    override fun loadStopPoints(location: Location?) {
        location?.let {
            if (it.distanceTo(londonLocation()) < MAX_METERS) {
                interactor.loadStopPoints(it.altitude, it.longitude)
            } else {
                interactor.loadStopPoints(DEFAULT_LAT, DEFAULT_LON)
            }
        } ?: interactor.loadStopPoints(DEFAULT_LAT, DEFAULT_LON)
    }

    override fun loadArrivalTimes() {
        interactor.loadArrivalTimes()
    }

    private fun londonLocation(): Location = Location("LONDON").apply {
        latitude = DEFAULT_LAT
        longitude = DEFAULT_LON
    }
}
