package fr.ffnet.downloader.profile.fanfiction

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.profile.fanfiction.ProfileFanfictionViewModel.ProfileFanfictionsResult.ProfileHasFanfictions
import fr.ffnet.downloader.profile.fanfiction.ProfileFanfictionViewModel.ProfileFanfictionsResult.ProfileHasNoFanfictions
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.synced.FanfictionUIItem
import fr.ffnet.downloader.utils.UIBuilder

class ProfileFanfictionViewModel(
    private val databaseRepository: DatabaseRepository,
    private val UIBuilder: UIBuilder
) : ViewModel() {

    private lateinit var myFavoritesResult: LiveData<ProfileFanfictionsResult>
    fun getMyFavoritesList(): LiveData<ProfileFanfictionsResult> =
        myFavoritesResult

    private lateinit var myStoriesResult: LiveData<ProfileFanfictionsResult>
    fun getMyStoriesList(): LiveData<ProfileFanfictionsResult> = myStoriesResult

    fun loadFavoriteFanfictions(authorId: String) {
        myFavoritesResult = Transformations.map(databaseRepository.getFavoriteFanfictions(authorId)) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                ProfileHasFanfictions(
                    fanfictionList.map {
                        UIBuilder.buildFanfictionUI(
                            fanfiction = it,
                            shouldShowAuthor = true
                        )
                    }
                )
            } else {
                ProfileHasNoFanfictions
            }
        }
    }

    fun loadMyFanfictions(authorId: String) {
        myStoriesResult = Transformations.map(databaseRepository.getStories(authorId)) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                ProfileHasFanfictions(
                    fanfictionList.map {
                        UIBuilder.buildFanfictionUI(it)
                    }
                )
            } else ProfileHasNoFanfictions
        }
    }

    sealed class ProfileFanfictionsResult {
        data class ProfileHasFanfictions(
            val fanfictionUIList: List<FanfictionUIItem.FanfictionUI>
        ) : ProfileFanfictionsResult()

        object ProfileHasNoFanfictions : ProfileFanfictionsResult()
    }
}
