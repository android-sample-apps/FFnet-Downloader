package com.citymapper.codingchallenge.stoppoints

data class StopPoint(
    val id: String,
    val name: String,
    var arrivalTimes: List<Arrival> = emptyList()
)

data class Arrival(
    val id: String,
    val time: Int
)
