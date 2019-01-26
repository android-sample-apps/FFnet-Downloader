package com.citymapper.codingchallenge.line

import com.citymapper.codingchallenge.common.RepositoryException
import java.io.IOException

interface LineRepository {
    fun loadLine(line: String): Line
}

class LineRepositoryImpl(
    private val service: LineService
) : LineRepository {

    companion object {
        private const val DIRECTION = "inbound"
    }

    override fun loadLine(line: String): Line {
        try {
            service.getLine(line, DIRECTION).execute().body()?.let {
                return transformLine(it)
            } ?: throw RepositoryException()
        } catch (io: IOException) {
            throw RepositoryException(cause = io)
        }
    }

    private fun transformLine(line: LineJson): Line = Line(
        id = line.lineId,
        name = line.lineName,
        stations = transformStations(line.stations)
    )

    private fun transformStations(stations: List<StationJson>): List<Station> {
        return stations.map {
            Station(
                id = it.stationId ?: "",
                name = it.name ?: ""
            )
        }
    }
}
