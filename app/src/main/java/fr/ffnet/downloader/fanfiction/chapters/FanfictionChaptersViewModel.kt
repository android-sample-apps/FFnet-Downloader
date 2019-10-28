package fr.ffnet.downloader.fanfiction.chapters

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.ChapterUIModel
import fr.ffnet.downloader.repository.DatabaseRepository

class FanfictionChaptersViewModel(
    private val resources: Resources,
    private val dbRepository: DatabaseRepository
) : ViewModel() {

    private lateinit var chapterList: LiveData<List<ChapterUIModel>>

    fun getChapterList(): LiveData<List<ChapterUIModel>> = chapterList

    fun loadChapters(fanfictionId: String) {
        chapterList = Transformations.map(
            dbRepository.getChapters(fanfictionId)
        ) { chapterList ->
            chapterList.map {
                ChapterUIModel(
                    id = it.chapterId,
                    title = it.title,
                    status = resources.getString(
                        when (it.isSynced) {
                            true -> R.string.download_info_chapter_status_synced
                            false -> R.string.download_info_chapter_status_not_synced
                        }
                    )
                )
            }
        }
    }
}

class FanfictionChaptersViewModelFactory(
    private val creator: () -> FanfictionChaptersViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}
