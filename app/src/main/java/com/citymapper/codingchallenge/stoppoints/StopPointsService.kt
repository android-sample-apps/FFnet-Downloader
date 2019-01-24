package com.citymapper.codingchallenge.stoppoints

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StopPointsService {
    @GET("StopPoint")
    fun getStopPoints(
        @Query("stopTypes") stopTypes: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("radius") radius: Int
    ): Call<StopPointsDataJson>

    @GET("StopPoint")
    fun getStopPointsV2(
        @Query("stopTypes") stopTypes: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("radius") radius: Int
    ): Observable<StopPointsDataJson>

    @GET("StopPoint/{id}/Arrivals")
    fun getArrivals(@Path("id") id: String): Call<List<ArrivalJson>>

    @GET("StopPoint/{id}/Arrivals")
    fun getArrivalsV2(@Path("id") id: String): Observable<List<ArrivalJson>>
}
