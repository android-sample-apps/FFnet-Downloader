package com.citymapper.codingchallenge.stoppoints

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface StopPointsRepository {
    fun loadStopPoints(latitude: Double,
                       longitude: Double,
                       listener: RepositoryListener)

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

    @SuppressLint("CheckResult")
    override fun loadArrivalTimes(stopPoints: List<StopPoint>, listener: RepositoryListener) {

        val requests = ArrayList<Observable<*>>()

        stopPoints.map {
            requests.add(service.getArrivalsV2(it.id))
        }
        Observable.zip(requests) {

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<List<ArrivalJson>>() {
                override fun onComplete() {
                    TODO(
                        "not implemented"
                    ) //To change body of created functions use File | Settings | File Templates.
                }

                override fun onNext(t: List<ArrivalJson>) {
                    TODO(
                        "not implemented"
                    ) //To change body of created functions use File | Settings | File Templates.
                }

                override fun onError(e: Throwable) {
                    TODO(
                        "not implemented"
                    ) //To change body of created functions use File | Settings | File Templates.
                }

            })




        stopPoints.forEach { stopPoint ->
            val call = service.getArrivals(stopPoint.id)
            call.enqueue(object : Callback<List<ArrivalJson>> {
                override fun onFailure(call: Call<List<ArrivalJson>>, t: Throwable) {
                    println("NOPE")
                }

                override fun onResponse(call: Call<List<ArrivalJson>>,
                                        response: Response<List<ArrivalJson>>) {
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
