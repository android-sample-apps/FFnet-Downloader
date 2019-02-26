package fr.ffnet.downloader.downloader

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.R
import fr.ffnet.downloader.downloader.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.utils.UrlTransformer
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class DownloaderViewModel(
    private val urlTransformer: UrlTransformer,
    private val resources: Resources,
    private val dao: FanfictionDao,
    private val repository: DownloaderRepository
) : ViewModel() {

    private val currentFanfiction: MutableLiveData<FanfictionViewModel> by lazy {
        MutableLiveData<FanfictionViewModel>()
    }
    private lateinit var chapterList: LiveData<List<ChapterViewModel>>

    fun loadFanfictionInfos(url: String?) {
        if (!url.isNullOrEmpty()) {
            val urlTransformationResult = urlTransformer.getIdFromUrl(url)
            CoroutineScope(Dispatchers.IO).launch {
                when (urlTransformationResult) {
                    is UrlTransformSuccess -> loadFanfictionInfo(
                        urlTransformationResult.fanfictionId
                    )
                    is UrlTransformFailure -> TODO()
                }
            }
        }
    }

    fun getCurrentFanfiction(): LiveData<FanfictionViewModel> = currentFanfiction

    fun getChapterList(): LiveData<List<ChapterViewModel>> = chapterList

    private fun loadFanfictionInfo(fanfictionId: String) {
        val fanfictionResult = repository.loadFanfictionInfo(fanfictionId)
        if (fanfictionResult is FanfictionRepositoryResultSuccess) {
            with(fanfictionResult) {
                if (fanfictionInfo.chapterList.size > 1) {
                    chapterList = Transformations.map(
                        dao.getChapters(fanfictionId)
                    ) { chapterList ->
                        chapterList.map {
                            ChapterViewModel(
                                id = it.chapterId,
                                title = it.title,
                                status = resources.getString(
                                    when (it.isSynced) {
                                        true -> R.string.download_info_chapter_status_synced
                                        false -> R.string.download_info_chapter_status_unsynced
                                    }
                                )
                            )
                        }
                    }
                    repository.loadAllChapters(fanfictionInfo.id, fanfictionInfo.chapterList)
                }
                presentFanfictionInfo(fanfictionInfo)
            }
        } else {

        }
    }

    private fun presentFanfictionInfo(fanfictionInfo: Fanfiction) {
        with(fanfictionInfo) {
            val fanfictionViewModel = FanfictionViewModel(
                id = id,
                title = title,
                words = words.toString(),
                summary = summary,
                updatedDate = SimpleDateFormat("yyyy-mm-dd").format(updatedDate),
                publishedDate = SimpleDateFormat("yyyy-mm-dd").format(publishedDate),
                chapterList = chapterList.map {
                    ChapterViewModel(
                        id = it.id,
                        title = it.title,
                        status = resources.getString(R.string.download_info_chapter_status_unsynced)
                    )
                }
            )
            CoroutineScope(Dispatchers.Main).launch {
                currentFanfiction.value = fanfictionViewModel
            }
        }
    }

    private fun presentFanfictionInfoError() {

    }
}
