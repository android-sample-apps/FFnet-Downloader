package com.citymapper.codingchallenge.stoppoints

import androidx.room.TypeConverter

object ArrivalTimeConverter {

    @TypeConverter
    @JvmStatic
    fun arrivalTimeToArrivalDb(arrival: Arrival): ArrivalDb = ArrivalDb(
        id = arrival.id,
        time = arrival.time
    )


    @TypeConverter
    @JvmStatic
    fun arrivalDbToArrivalTime(arrival: ArrivalDb): Arrival? = Arrival(
        id = arrival.id,
        time = arrival.time
    )
}
