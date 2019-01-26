package com.citymapper.codingchallenge.line

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface LineService {
    @GET("Line/{id}/Route/Sequence/{direction}")
    fun getLine(
        @Path("id") id: String,
        @Path("direction") direction: String
    ): Call<LineJson>
}
