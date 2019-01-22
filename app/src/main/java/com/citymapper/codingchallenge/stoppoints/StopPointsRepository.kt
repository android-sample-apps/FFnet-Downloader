package com.citymapper.codingchallenge.stoppoints

import com.citymapper.codingchallenge.common.RepositoryException
import java.io.IOException

interface StopPointsRepository {
    fun loadStopPoints(altitude: Double, longitude: Double): List<StopPoint>
}

class StopPointsRepositoryImpl(
    private val service: StopPointsService
) : StopPointsRepository {

    companion object {
        private const val DEFAULT_RADIUS = 1000
        private const val DEFAULT_STOP_TYPE = "NaptanMetroStation"
    }

    override fun loadStopPoints(altitude: Double, longitude: Double): List<StopPoint> {
        try {
            service.getStopPoints(
                stopTypes = DEFAULT_STOP_TYPE,
                latitude = altitude,
                longitude = longitude,
                radius = DEFAULT_RADIUS
            ).execute().body()?.let {
                return transformToEntity(it.stopPoints)
            } ?: throw RepositoryException()
        } catch (io: IOException) {
            throw RepositoryException(cause = io)
        }
    }

    private fun transformToEntity(stopPoints: List<StopPointJson>): List<StopPoint> = stopPoints.map {
        StopPoint(
            id = it.id,
            name = it.commonName
        )
    }
}
