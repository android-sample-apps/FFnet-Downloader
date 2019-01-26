package com.citymapper.codingchallenge.line

class LineInteractor(
    private val presenter: LinePresenter,
    private val repository: LineRepository
) {
    fun loadLine(lineId: String, stationId: String) {
        val lineId = repository.loadLine(lineId)
        presenter.presentLine(lineId, stationId)
    }
}
