package com.citymapper.codingchallenge.line

interface LineController {
    fun loadLine(lineId: String, stationId: String)
}

class LineControllerImpl(private val interactor: LineInteractor): LineController {
    override fun loadLine(lineId: String, stationId: String) {
        interactor.loadLine(lineId, stationId)
    }
}
