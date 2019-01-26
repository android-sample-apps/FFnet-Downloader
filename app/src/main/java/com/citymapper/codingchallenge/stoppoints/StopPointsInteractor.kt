package com.citymapper.codingchallenge.stoppoints

interface RepositoryListener {
    fun onArrivalTimesLoaded(stopPoints: List<StopPoint>)
}

class StopPointsInteractor(
    private val presenter: StopPointsPresenter,
    private val repository: StopPointsRepository
): RepositoryListener {

    private var stopPoints: List<StopPoint> = emptyList()

    fun loadStopPoints(altitude: Double, longitude: Double) {
        stopPoints = emptyList()
        stopPoints = repository.loadStopPoints(altitude, longitude)
        repository.loadArrivalTimes(stopPoints, this)
        presenter.presentStopPoints(stopPoints)
    }

    fun loadArrivalTimes() {
        if (stopPoints.isNotEmpty()) {
            repository.loadArrivalTimes(stopPoints, this)
        }
    }

    override fun onArrivalTimesLoaded(stopPoints: List<StopPoint>) {
        this.stopPoints = stopPoints
        presenter.presentStopPoints(stopPoints)
    }
}
