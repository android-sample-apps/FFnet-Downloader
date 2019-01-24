package com.citymapper.codingchallenge.stoppoints

interface StopPointsInteractor {
    fun loadStopPoints(altitude: Double, longitude: Double)
    fun loadArrivalTimes()
}

interface ArrivalTimesListener {
    fun onArrivalTimesLoaded(stopPointId: String, arrivalTimes: List<Arrival>)
}

class StopPointsInteractorImpl(
    private val presenter: StopPointsPresenter,
    private val repository: StopPointsRepository,
    private val database: DbStopPointsRepository
) : StopPointsInteractor, ArrivalTimesListener {

    override fun loadStopPoints(altitude: Double, longitude: Double) {
        database.deleteAll()
        val stopPoints = repository.loadStopPoints(altitude, longitude)
        database.addStopPoints(stopPoints)
        presenter.presentStopPoints(stopPoints)
    }

    override fun loadArrivalTimes() {
        val stopPoints = database.getStopPoints()
        if (stopPoints.isNotEmpty()) {
            repository.loadArrivalTimes(stopPoints, this)
        }
    }

    override fun onArrivalTimesLoaded(stopPointId: String, arrivalTimes: List<Arrival>) {
        val stopPoints = database.getStopPoints()
        if (stopPoints.isNotEmpty()) {
            val updatedStopPoints = database.updateArrivalTimes(stopPointId, arrivalTimes)
            presenter.presentStopPoints(updatedStopPoints)
        }
    }
}
