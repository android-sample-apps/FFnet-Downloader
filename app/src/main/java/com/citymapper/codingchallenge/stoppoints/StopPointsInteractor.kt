package com.citymapper.codingchallenge.stoppoints

interface StopPointsInteractor {
    fun loadStopPoints(altitude: Double, longitude: Double)
    fun loadArrivalTimes()
}

interface RepositoryListener {
    fun onStopPointsLoaded(stopPoints: List<StopPoint>)
    fun onArrivalTimesLoaded(stopPoints: List<StopPoint>)
}

class StopPointsInteractorImpl(
    private val presenter: StopPointsPresenter,
    private val repository: StopPointsRepository
) : StopPointsInteractor, RepositoryListener {

    private var stopPoints: List<StopPoint> = emptyList()

    override fun loadStopPoints(altitude: Double, longitude: Double) {
        stopPoints = emptyList()
        repository.loadStopPoints(altitude, longitude, this)
    }

    override fun loadArrivalTimes() {
        if (stopPoints.isNotEmpty()) {
            repository.loadArrivalTimes(stopPoints, this)
        }
    }

    override fun onStopPointsLoaded(stopPoints: List<StopPoint>) {
        repository.loadArrivalTimes(stopPoints, this)
        presenter.presentStopPoints(stopPoints)
    }

    override fun onArrivalTimesLoaded(stopPoints: List<StopPoint>) {
        this.stopPoints = stopPoints
        presenter.presentStopPoints(stopPoints)
    }
}
