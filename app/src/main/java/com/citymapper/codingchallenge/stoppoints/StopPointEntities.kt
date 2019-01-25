package com.citymapper.codingchallenge.stoppoints

data class StopPoint(
    val id: String,
    val name: String,
    val lines: List<Line>,
    var arrivalTimes: List<Arrival> = emptyList()
)

data class Line(
    val id: String
)

data class Arrival(
    val id: String,
    val time: Int
)
