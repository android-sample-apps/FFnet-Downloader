package com.citymapper.codingchallenge.line

data class LineModel(
    val name: String,
    val stations: List<StationModel>
)

data class StationModel(
    val id: String,
    val name: String,
    val isSelectedStation: Boolean
)
