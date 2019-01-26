package com.citymapper.codingchallenge.stoppoints

interface StopPointsPresenter {
    fun presentStopPoints(stopPoints: List<StopPoint>)
}

interface StopPointsView {
    fun displayStopPoints(stopPoints: List<StopPointModel>)
}

class StopPointsPresenterImpl(
    private val display: StopPointsView
) : StopPointsPresenter {
    override fun presentStopPoints(stopPoints: List<StopPoint>) {
        display.displayStopPoints(stopPoints.map { stopPoint ->
            StopPointModel(
                id = stopPoint.id,
                name = stopPoint.name.replace("Underground Station", "").trim(),
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
