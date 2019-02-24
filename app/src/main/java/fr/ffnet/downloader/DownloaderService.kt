package fr.ffnet.downloader

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DownloaderService {
    @GET("s/{storyId}/{chapterId}")
    fun getPage(
        @Path("storyId") storyId: String,
        @Path("chapterId") chapterId: Int? = 1
    ): Call<ResponseBody>
}
