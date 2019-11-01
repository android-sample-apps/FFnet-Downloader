package fr.ffnet.downloader.repository

import android.content.Context
import androidx.concurrent.futures.ResolvableFuture
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import fr.ffnet.downloader.fanfictionutils.RecipeBuilder
import fr.ffnet.downloader.repository.dao.RecipeDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch

class DownloaderWorker(
    context: Context,
    workerParams: WorkerParameters
) : ListenableWorker(context, workerParams) {

    companion object {
        const val RECIPE_ID_KEY = "RECIPE_ID"
    }

    lateinit var service: CrawlService
    lateinit var recipeBuilder: RecipeBuilder
    lateinit var recipeDao: RecipeDao

    private val future: ResolvableFuture<Result> = ResolvableFuture.create()

    override fun startWork(): ListenableFuture<Result> {
        CoroutineScope(Default).launch {

        }
        return future
    }
}
