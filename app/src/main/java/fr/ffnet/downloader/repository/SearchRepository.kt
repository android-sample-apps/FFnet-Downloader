package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import fr.ffnet.downloader.fanfictionutils.FanfictionBuilder
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.search.Chapter
import fr.ffnet.downloader.search.Fanfiction
import okhttp3.ResponseBody
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DownloaderRepository(
    private val service: SearchService,
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
                dao.insertInHistory(
                    HistoryEntity(
                        fanfictionInfo.id,
                        fanfictionInfo.title,
                        DateTime.now().toDate()
                    )
                )

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

    fun loadHistory(): LiveData<List<HistoryEntity>> = dao.getHistory()

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

    private fun Fanfiction.toFanfictionEntity(): FanfictionEntity {
        return FanfictionEntity(
            id = id,
            title = title,
            words = words,
            summary = summary,
            publishedDate = publishedDate,
            updatedDate = updatedDate,
            syncedDate = DateTime.now().toDate()
        )
    }

    sealed class FanfictionRepositoryResult {
        data class FanfictionRepositoryResultSuccess(val fanfictionInfo: Fanfiction) : FanfictionRepositoryResult()
        object FanfictionRepositoryResultFailure : FanfictionRepositoryResult()
    }
}
