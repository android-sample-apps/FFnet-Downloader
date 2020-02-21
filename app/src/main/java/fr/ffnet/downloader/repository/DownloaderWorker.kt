package fr.ffnet.downloader.repository

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import fr.ffnet.downloader.fanfictionutils.FanfictionBuilder
import fr.ffnet.downloader.repository.dao.FanfictionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DownloaderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val FANFICTION_ID_KEY = "FANFICTION_ID"
    }

    lateinit var service: CrawlService
    lateinit var fanfictionBuilder: FanfictionBuilder
    lateinit var fanfictionDao: FanfictionDao

    override suspend fun doWork(): Result {
        CoroutineScope(Dispatchers.Default).launch {
            val fanfictionId = inputData.getString(FANFICTION_ID_KEY) ?: ""
            val chapterList = fanfictionDao.getChaptersToSync(fanfictionId)
            chapterList.forEachIndexed { index, chapter ->
                service.getFanfiction(
                    fanfictionId, chapter.chapterId
                ).enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        // Do nothing
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                val chapterContent = fanfictionBuilder.extractChapter(it.string())
                                Thread {
                                    fanfictionDao.updateChapter(
                                        content = chapterContent,
                                        isSynced = true,
                                        chapterId = chapter.chapterId,
                                        fanfictionId = fanfictionId
                                    )
                                }.start()
                            }
                            if (index == chapterList.lastIndex) {
                                Result.success()
                            }
                        }
                    }
                })
            }
        }
        return Result.success()
    }
}