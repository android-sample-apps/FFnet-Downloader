package fr.ffnet.downloader.downloader

import fr.ffnet.downloader.downloader.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultFailure
import fr.ffnet.downloader.downloader.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DownloaderRepository(
    private val service: DownloaderService,
    private val fanfictionBuilder: FanfictionBuilder,
    private val dao: FanfictionDao
) {

    fun loadFanfictionInfo(id: String): FanfictionRepositoryResult {

        val response = service.getPage(id).execute()
        return if (response.isSuccessful) {
            response.body()?.let {

                val fanfictionInfo = fanfictionBuilder.buildFanfiction(id, it.string())
                dao.insertFanfiction(fanfictionInfo.toFanfictionEntity())

                FanfictionRepositoryResultSuccess(fanfictionInfo)
            } ?: FanfictionRepositoryResultFailure
        } else {
            FanfictionRepositoryResultFailure
        }
    }

    fun loadAllChapters(
        fanfictionId: String,
        chapterList: List<Chapter>
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
                        response.body()?.let {
                            val chapterContent = fanfictionBuilder.extractChapter(it.string())
                            Thread {
                                dao.insertChapterList(listOf(
                                    ChapterEntity(
                                        fanfictionId = fanfictionId,
                                        chapterId = chapter.id,
                                        title = chapter.title,
                                        content = chapterContent
                                    )
                                ))
                            }.start()
                        } ?: println("onResponse Nope")
                    } else {
                        println("onResponse Nope")
                    }
                }
            })
        }
    }

    sealed class FanfictionRepositoryResult {
        data class FanfictionRepositoryResultSuccess(val fanfictionInfo: Fanfiction) : FanfictionRepositoryResult()
        object FanfictionRepositoryResultFailure : FanfictionRepositoryResult()
    }
}

private fun Fanfiction.toFanfictionEntity(): FanfictionEntity {
    return FanfictionEntity(
        id = id,
        title = title,
        words = words,
        summary = summary
    )
}
