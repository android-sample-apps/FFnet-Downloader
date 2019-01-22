package com.citymapper.codingchallenge.stoppoints

interface StopPointsInteractor {
    fun loadStopPoints(altitude: Double, longitude: Double)
}

class StopPointsInteractorImpl(
    private val presenter: StopPointsPresenter,
    private val repository: StopPointsRepository
) : StopPointsInteractor {
    override fun loadStopPoints(altitude: Double, longitude: Double) {
        val stopPoints = repository.loadStopPoints(altitude, longitude)
        presenter.presentStopPoints(stopPoints)
    }
}
