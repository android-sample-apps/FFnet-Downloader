package fr.ffnet.downloader.repository

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MobileCrawlService {
    @GET("search/?ready=1&ready=1")
    fun search(
        @Query("keywords") keywords: String,
        @Query("type") searchType: String
    ): Call<ResponseBody>
}
