package fr.ffnet.downloader.repository

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import fr.ffnet.downloader.utils.FanfictionBuilder
import fr.ffnet.downloader.repository.dao.FanfictionDao

class DownloaderWorkerFactory(
    private val service: CrawlService,
    private val fanfictionBuilder: FanfictionBuilder,
    private val fanfictionDao: FanfictionDao
) : WorkerFactory() {
    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
        val workerClass = Class.forName(workerClassName).asSubclass(ListenableWorker::class.java)
        val constructor = workerClass.getDeclaredConstructor(Context::class.java, WorkerParameters::class.java)
        val instance = constructor.newInstance(appContext, workerParameters)
        when (instance) {
            is DownloaderWorker -> {
                instance.service = service
                instance.fanfictionBuilder = fanfictionBuilder
                instance.fanfictionDao = fanfictionDao
            }
        }
        return instance
    }
}