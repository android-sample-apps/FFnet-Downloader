package fr.ffnet.downloader.search

import fr.ffnet.downloader.search.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultFailure
import fr.ffnet.downloader.search.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DownloaderRepository(
    private val service: DownloaderService,
    private val fanfictionBuilder: FanfictionBuilder,
    private val dao: FanfictionDao
) {

    fun loadFanfictionInfo(fanfictionId: String): FanfictionRepositoryResult {

        val response = service.getPage(fanfictionId).execute()
        return if (response.isSuccessful) {
            response.body()?.let { responseBody ->

                val existingChapters = dao.getChapters(fanfictionId).map { it.chapterId }
                val fanfictionInfo = fanfictionBuilder.buildFanfiction(
                    fanfictionId, responseBody.string(), existingChapters
                )
                dao.insertFanfiction(fanfictionInfo.toFanfictionEntity())

                if (existingChapters.isNotEmpty()) {
                    fanfictionInfo.chapterList.filter { it.id !in existingChapters }.map { chapter ->
                        insertChapter(fanfictionId, chapter)
                    }
                } else {
                    fanfictionInfo.chapterList.map { chapter ->
                        insertChapter(fanfictionId, chapter)
                    }
                }
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
                        response.body()?.let {
                            val chapterContent = fanfictionBuilder.extractChapter(it.string())
                            Thread {
                                dao.updateChapter(
                                    content = chapterContent,
                                    isSynced = true,
                                    chapterId = chapter.id,
                                    fanfictionId = fanfictionId
                                )
                            }.start()
                        } ?: println("onResponse Nope")
                    } else {
                        println("onResponse Nope")
                    }
                }
            })
        }
    }

    private fun insertChapter(fanfictionId: String, chapter: Chapter) {
        dao.insertChapterList(
            listOf(
                ChapterEntity(
                    fanfictionId = fanfictionId,
                    chapterId = chapter.id,
                    title = chapter.title
                )
            )
        )
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
        summary = summary,
        publishedDate = publishedDate,
        updatedDate = updatedDate
    )
}
