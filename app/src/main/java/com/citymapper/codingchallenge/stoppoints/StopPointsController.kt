package com.citymapper.codingchallenge.stoppoints

import android.location.Location

interface StopPointsController {
    fun loadStopPoints(location: Location?)
}

class StopPointsControllerImpl(private val interactor: StopPointsInteractor) : StopPointsController {

    companion object {
        private const val DEFAULT_LAT = 51.512
        private const val DEFAULT_LON = -0.127
        private val LONDON_LOCATION = londonLocation()

        private fun londonLocation(): Location = Location("LONDON").apply {
            latitude = DEFAULT_LAT
            longitude = DEFAULT_LON
        }
    }

    override fun loadStopPoints(location: Location?) {
        location?.let {
            val distanceToLondon = it.distanceTo(londonLocation())
            interactor.loadStopPoints(it.altitude, it.longitude)
        } ?: interactor.loadStopPoints(DEFAULT_LAT, DEFAULT_LON)
    }
}
