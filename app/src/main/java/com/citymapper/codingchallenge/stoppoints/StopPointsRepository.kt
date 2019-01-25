package com.citymapper.codingchallenge.stoppoints

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

interface StopPointsRepository {
    fun loadStopPoints(
        latitude: Double,
        longitude: Double,
        listener: RepositoryListener
    )

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
        longitude: Double,
        listener: RepositoryListener
    ) {
        CompositeDisposable().add(
            service.getStopPointsV2(
                stopTypes = DEFAULT_STOP_TYPE,
                latitude = latitude,
                longitude = longitude,
                radius = DEFAULT_RADIUS
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<StopPointsDataJson>() {
                    override fun onError(e: Throwable) {
                        Log.d("RX", "onError")
                    }

                    override fun onNext(stopPointsJson: StopPointsDataJson) {
                        Log.d("RX", "onNext ${stopPointsJson.stopPoints}")
                        val stopPoints = transformStopPoints(stopPointsJson.stopPoints)
                        listener.onStopPointsLoaded(stopPoints)
                    }

                    override fun onComplete() {
                        Log.d("RX", "onComplete")
                    }
                })
        )
    }

    override fun loadArrivalTimes(stopPoints: List<StopPoint>, listener: RepositoryListener) {
        val requests = ArrayList<Observable<List<ArrivalJson>>>()
        stopPoints.map {
            requests.add(service.getArrivalsV2(it.id))
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
                    // Do stuff
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

    private fun transformStopPoints(stopPoints: List<StopPointJson>): List<StopPoint> = stopPoints.map {
        StopPoint(
            id = it.id,
            name = it.commonName,
            lines = it.lines.map { line ->
                Line(
                    id = line.id
                )
            }
        )
    }
}
