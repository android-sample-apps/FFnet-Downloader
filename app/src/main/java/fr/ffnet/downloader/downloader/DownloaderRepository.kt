package fr.ffnet.downloader.downloader

import fr.ffnet.downloader.downloader.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultFailure
import fr.ffnet.downloader.downloader.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class DownloaderRepository @Inject constructor(
    private val service: DownloaderService
) {

    fun loadFanfictionInfo(id: String): FanfictionRepositoryResult {

        val response = service.getPage(id).execute()
        return if (response.isSuccessful) {
            response.body()?.let {
                FanfictionRepositoryResultSuccess(it.string())
            } ?: FanfictionRepositoryResultFailure
        } else {
            FanfictionRepositoryResultFailure
        }
    }

    fun loadAllChapters(
        fanfictionId: String,
        chapterList: List<Chapter>,
        listener: ChapterLoadedListener
    ) {
        chapterList.forEach { chapter ->
            println("Adding request for chapter ${chapter.id}")
            service.getPage(fanfictionId, chapter.id).enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    println("onFailure Nope")
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        println("OK")
                        listener.onChapterLoaded(
                            chapter,
                            response.body()?.string() ?: ""
                        )
                    } else {
                        println("onResponse Nope")
                    }
                }
            })
        }
    }

    sealed class FanfictionRepositoryResult {
        data class FanfictionRepositoryResultSuccess(val html: String) : FanfictionRepositoryResult()
        object FanfictionRepositoryResultFailure : FanfictionRepositoryResult()
    }

    interface ChapterLoadedListener {
        fun onChapterLoaded(chapter: Chapter, html: String)
    }
}
