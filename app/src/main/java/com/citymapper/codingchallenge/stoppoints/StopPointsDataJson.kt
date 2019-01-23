package com.citymapper.codingchallenge.stoppoints

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StopPointsDataJson(
    val stopPoints: List<StopPointJson>
)

@JsonClass(generateAdapter = true)
data class StopPointJson(
    val id: String,
    val commonName: String
)

@JsonClass(generateAdapter = true)
data class ArrivalJson(
    val id: String,
    val timeToStation: Int,
    val naptanId: String,
    val lineId: String
)
