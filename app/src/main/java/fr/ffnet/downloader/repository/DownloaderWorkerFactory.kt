package fr.ffnet.downloader.repository

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import fr.ffnet.downloader.fanfictionutils.RecipeBuilder
import fr.ffnet.downloader.repository.dao.RecipeDao

class DownloaderWorkerFactory(
    private val service: CrawlService,
    private val recipeBuilder: RecipeBuilder,
    private val recipeDao: RecipeDao
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val workerClass = Class.forName(workerClassName).asSubclass(ListenableWorker::class.java)
        val constructor = workerClass.getDeclaredConstructor(
            Context::class.java,
            WorkerParameters::class.java
        )
        val instance = constructor.newInstance(appContext, workerParameters)
        when (instance) {
            is DownloaderWorker -> {
                instance.service = service
                instance.recipeBuilder = recipeBuilder
                instance.recipeDao = recipeDao
            }
        }
        return instance
    }
}