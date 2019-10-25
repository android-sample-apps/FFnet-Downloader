package fr.ffnet.downloader.profile.fanfiction

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.ffnet.downloader.R
import fr.ffnet.downloader.profile.FanfictionSyncedUIModel
import fr.ffnet.downloader.profile.ProfileViewModel
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.search.Fanfiction
import fr.ffnet.downloader.utils.DateFormatter

class ProfileFanfictionViewModel(
    private val resources: Resources,
    private val databaseRepository: DatabaseRepository,
    private val dateFormatter: DateFormatter
) : ViewModel() {

    private lateinit var myFavoritesResult: LiveData<ProfileViewModel.ProfileFanfictionsResult>
    fun getMyFavoritesList(): LiveData<ProfileViewModel.ProfileFanfictionsResult> =
        myFavoritesResult

    private lateinit var myStoriesResult: LiveData<ProfileViewModel.ProfileFanfictionsResult>
    fun getMyStoriesList(): LiveData<ProfileViewModel.ProfileFanfictionsResult> = myStoriesResult

    fun loadFavoriteFanfictions() {
        myFavoritesResult = Transformations.map(
            databaseRepository.getMyFavoriteFanfictions()
        ) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                ProfileViewModel.ProfileFanfictionsResult.ProfileHasFanfictions(
                    buildFanfictionSyncedUIModelList(fanfictionList)
                )
            } else {
                ProfileViewModel.ProfileFanfictionsResult.ProfileHasNoFanfictions
            }
        }
    }

    fun loadMyFanfictions() {
        myStoriesResult = Transformations.map(
            databaseRepository.getMyFanfictions()
        ) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                ProfileViewModel.ProfileFanfictionsResult.ProfileHasFanfictions(
                    buildFanfictionSyncedUIModelList(fanfictionList)
                )
            } else {
                ProfileViewModel.ProfileFanfictionsResult.ProfileHasNoFanfictions
            }
        }
    }

    private fun buildFanfictionSyncedUIModelList(fanfictionList: List<Fanfiction>): List<FanfictionSyncedUIModel> {
        return fanfictionList.map { fanfiction ->
            FanfictionSyncedUIModel(
                id = fanfiction.id,
                title = fanfiction.title,
                updatedDate = dateFormatter.format(fanfiction.updatedDate),
                publishedDate = dateFormatter.format(fanfiction.publishedDate),
                fetchedDate = dateFormatter.format(fanfiction.fetchedDate),
                chapters = resources.getString(
                    R.string.synced_fanfictions_chapters,
                    fanfiction.nbSyncedChapters,
                    fanfiction.nbChapters
                )
            )
        }
    }
}

class ProfileFanfictionViewModelFactory(
    private val creator: () -> ProfileFanfictionViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}
