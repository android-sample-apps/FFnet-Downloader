package com.citymapper.codingchallenge.line

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LineJson(
    val lineId: String,
    val lineName: String,
    val stations: List<StationJson>
)

@JsonClass(generateAdapter = true)
data class StationJson(
    val stationId: String?,
    val name: String?
)
