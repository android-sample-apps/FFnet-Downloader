package com.citymapper.codingchallenge.stoppoints

import com.citymapper.codingchallenge.StationNameTransformer

interface StopPointsPresenter {
    fun presentStopPoints(stopPoints: List<StopPoint>)
}

interface StopPointsView {
    fun displayStopPoints(stopPoints: List<StopPointModel>)
}

class StopPointsPresenterImpl(
    private val display: StopPointsView,
    private val stationNameTransformer: StationNameTransformer
) : StopPointsPresenter {
    override fun presentStopPoints(stopPoints: List<StopPoint>) {
        display.displayStopPoints(stopPoints.map { stopPoint ->
            StopPointModel(
                id = stopPoint.id,
                name = stationNameTransformer.format(stopPoint.name),
                displayLine = stopPoint.lines[0],
                arrivalTimes = stopPoint.arrivalTimes.map { arrival ->
                    ArrivalModel(
                        id = arrival.id,
                        time = formatSeconds(arrival.time)
                    )
                }
            )
        })
    }

    private fun formatSeconds(seconds: Int): String {
        return String.format("%02d:%02d", seconds / 60, seconds % 60)
    }
}
