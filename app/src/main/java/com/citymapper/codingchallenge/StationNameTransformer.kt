package com.citymapper.codingchallenge

import javax.inject.Inject

class StationNameTransformer @Inject constructor() {
    fun format(stationName: String): String {
        return stationName.replace("Underground Station", "").trim()
    }
}
