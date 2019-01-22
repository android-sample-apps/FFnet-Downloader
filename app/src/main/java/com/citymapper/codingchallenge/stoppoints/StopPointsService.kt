package com.citymapper.codingchallenge.stoppoints

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StopPointsService {
    @GET("StopPoint")
    fun getStopPoints(
        @Query("stopTypes") stopTypes: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("radius") radius: Int
    ): Call<StopPointsDataJson>
}
