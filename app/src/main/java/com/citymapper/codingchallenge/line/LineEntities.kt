package com.citymapper.codingchallenge.line

data class Line(
    val id: String,
    val name: String,
    val stations: List<Station>
)

data class Station(
    val id: String,
    val name: String
)
