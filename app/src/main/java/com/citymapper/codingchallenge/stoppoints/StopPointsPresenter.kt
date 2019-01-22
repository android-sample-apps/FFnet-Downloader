package com.citymapper.codingchallenge.stoppoints

interface StopPointsPresenter {
    fun presentStopPoints(stopPoints: List<StopPoint>)
}

interface StopPointsView {
    fun displayStopPoints(stopPoints: List<StopPoint>)
}

class StopPointsPresenterImpl(private val display: StopPointsView) : StopPointsPresenter {
    override fun presentStopPoints(stopPoints: List<StopPoint>) {
        display.displayStopPoints(stopPoints)
    }
}
