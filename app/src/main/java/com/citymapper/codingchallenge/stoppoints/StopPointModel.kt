package com.citymapper.codingchallenge.stoppoints

data class StopPointModel(
    val id: String,
    val name: String,
    val displayLine: String,
    val arrivalTimes: List<ArrivalModel>
)

data class ArrivalModel(
    val id: String,
    val time: String
)
