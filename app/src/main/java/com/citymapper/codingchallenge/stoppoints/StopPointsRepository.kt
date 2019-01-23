package com.citymapper.codingchallenge.stoppoints

import com.citymapper.codingchallenge.common.RepositoryException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

interface StopPointsRepository {
    fun loadStopPoints(altitude: Double, longitude: Double): List<StopPoint>
    fun loadArrivalTimes(stopPoints: List<StopPoint>, listener: ArrivalTimesListener)
}

class StopPointsRepositoryImpl(
    private val service: StopPointsService
) : StopPointsRepository {

    companion object {
        private const val NB_TIMES = 3
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
                return transformStopPoints(it.stopPoints)
            } ?: throw RepositoryException()
        } catch (io: IOException) {
            throw RepositoryException(cause = io)
        }
    }

    override fun loadArrivalTimes(stopPoints: List<StopPoint>, listener: ArrivalTimesListener) {
        stopPoints.forEach { stopPoint ->
            val call = service.getArrivals(stopPoint.id)
            call.enqueue(object : Callback<List<ArrivalJson>> {
                override fun onFailure(call: Call<List<ArrivalJson>>, t: Throwable) {
                    println("NOPE")
                }

                override fun onResponse(call: Call<List<ArrivalJson>>, response: Response<List<ArrivalJson>>) {
                    response.body()?.let {
                        listener.onArrivalTimesLoaded(stopPoint.id, transformArrivalTimes(it))
                    }
                }
            })
        }
    }

    private fun transformArrivalTimes(arrivalTimes: List<ArrivalJson>): List<Arrival> {
        return arrivalTimes.sortedBy {
            it.timeToStation
        }.take(NB_TIMES).map {
            Arrival(
                id = it.id,
                time = it.timeToStation
            )
        }
    }

    private fun transformStopPoints(stopPoints: List<StopPointJson>): List<StopPoint> = stopPoints.map {
        StopPoint(
            id = it.id,
            name = it.commonName
        )
    }
}
