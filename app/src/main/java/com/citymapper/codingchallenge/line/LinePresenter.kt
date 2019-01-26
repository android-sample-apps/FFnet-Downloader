package com.citymapper.codingchallenge.line

import android.content.res.Resources
import com.citymapper.codingchallenge.R

interface LinePresenter {
    fun presentLine(line: Line, stationId: String)
}

interface LineView {
    fun showLine(line: LineModel)
}

class LinePresenterImpl(
    private val display: LineView,
    private val resources: Resources
) : LinePresenter {
    override fun presentLine(line: Line, stationId: String) {
        display.showLine(LineModel(
            name = line.name,
            stations = line.stations.map { station ->
                StationModel(
                    id = station.id,
                    name = resources.getString(
                        R.string.line_station_name,
                        station.name,
                        station.id
                    ),
                    isSelectedStation = station.id == stationId
                )
            }
        ))
    }
}
