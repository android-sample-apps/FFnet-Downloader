package com.citymapper.codingchallenge.stoppoints

import com.citymapper.codingchallenge.common.RepositoryException
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.*

interface StopPointsRepository {
    fun loadStopPoints(
        latitude: Double,
        longitude: Double
    ): List<StopPoint>

    fun loadArrivalTimes(stopPoints: List<StopPoint>, listener: RepositoryListener)
}

class StopPointsRepositoryImpl(
    private val service: StopPointsService
) : StopPointsRepository {

    companion object {
        private const val NB_TIMES = 3
        private const val DEFAULT_RADIUS = 1000
        private const val DEFAULT_STOP_TYPE = "NaptanMetroStation"
    }

    override fun loadStopPoints(
        latitude: Double,
        longitude: Double
    ): List<StopPoint> {
        try {
            service.getStopPoints(
                stopTypes = DEFAULT_STOP_TYPE,
                latitude = latitude,
                longitude = longitude,
                radius = DEFAULT_RADIUS
            ).execute().body()?.let {
                return transformStopPoints(it.stopPoints)
            } ?: throw RepositoryException()
        } catch (io: IOException) {
            throw RepositoryException(cause = io)
        }
    }

    override fun loadArrivalTimes(stopPoints: List<StopPoint>, listener: RepositoryListener) {
        val requests = ArrayList<Observable<List<ArrivalJson>>>()
        stopPoints.map {
            requests.add(service.getArrivals(it.id))
        }
        CompositeDisposable().add(
            Observable.zip(requests) { args -> Arrays.asList(args) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it[0].map { arrivalTimes ->
                        if (arrivalTimes is List<*>) {
                            val times: List<ArrivalJson> = arrivalTimes.filterIsInstance<ArrivalJson>()
                            if (times.isNotEmpty()) {
                                val stopPointId = stopPoints.indexOf(stopPoints.find {
                                    it.id == times[0].naptanId
                                })
                                stopPoints[stopPointId].arrivalTimes = transformArrivalTimes(times)
                            }
                        }
                    }
                    listener.onArrivalTimesLoaded(stopPoints)
                }, {
                    throw RepositoryException(cause = it.cause)
                })
        )
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

    private fun transformStopPoints(stopPoints: List<StopPointJson>): List<StopPoint> = stopPoints.map { stopPoint ->
        StopPoint(
            id = stopPoint.id,
            name = stopPoint.commonName,
            lines = stopPoint.lines.map { it.id }
        )
    }
}
