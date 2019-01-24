package com.citymapper.codingchallenge.stoppoints

interface StopPointsInteractor {
    fun loadStopPoints(altitude: Double, longitude: Double)
    fun loadArrivalTimes()
}

interface RepositoryListener {
    fun onStopPointsLoaded(stopPoints: List<StopPoint>)
    fun onArrivalTimesLoaded(stopPointId: String, arrivalTimes: List<Arrival>)
}

class StopPointsInteractorImpl(
    private val presenter: StopPointsPresenter,
    private val repository: StopPointsRepository
) : StopPointsInteractor, RepositoryListener {

    private var stopPoints: List<StopPoint> = emptyList()

    override fun loadStopPoints(altitude: Double, longitude: Double) {
        stopPoints = emptyList()
        stopPoints = repository.loadStopPoints(altitude, longitude, this)
        presenter.presentStopPoints(stopPoints)
    }

    override fun loadArrivalTimes() {
        if (stopPoints.isNotEmpty()) {
            repository.loadArrivalTimes(stopPoints, this)
        }
    }

    override fun onStopPointsLoaded(stopPoints: List<StopPoint>) {
        presenter.presentStopPoints(stopPoints)
    }

    override fun onArrivalTimesLoaded(stopPointId: String, arrivalTimes: List<Arrival>) {
        stopPoints = stopPoints.map {
            if (it.id == stopPointId) {
                StopPoint(
                    id = it.id,
                    name = it.name,
                    arrivalTimes = arrivalTimes
                )
            } else {
                it
            }
        }
        presenter.presentStopPoints(stopPoints)
    }
}
