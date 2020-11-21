package fr.ffnet.downloader.profile.fanfiction

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.profile.ProfileViewModel
import fr.ffnet.downloader.profile.ProfileViewModel.ProfileFanfictionsResult.ProfileHasFanfictions
import fr.ffnet.downloader.profile.ProfileViewModel.ProfileFanfictionsResult.ProfileHasNoFanfictions
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.UIBuilder

class ProfileFanfictionViewModel(
    private val databaseRepository: DatabaseRepository,
    private val UIBuilder: UIBuilder
) : ViewModel() {

    private lateinit var myFavoritesResult: LiveData<ProfileViewModel.ProfileFanfictionsResult>
    fun getMyFavoritesList(): LiveData<ProfileViewModel.ProfileFanfictionsResult> =
        myFavoritesResult

    private lateinit var myStoriesResult: LiveData<ProfileViewModel.ProfileFanfictionsResult>
    fun getMyStoriesList(): LiveData<ProfileViewModel.ProfileFanfictionsResult> = myStoriesResult

    fun loadFavoriteFanfictions() {
        myFavoritesResult = Transformations.map(databaseRepository.getMyFavoriteFanfictions()) { fanfictionList ->
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

    fun loadMyFanfictions() {
        myStoriesResult = Transformations.map(databaseRepository.getMyFanfictions()) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                ProfileHasFanfictions(
                    fanfictionList.map {
                        UIBuilder.buildFanfictionUI(it)
                    }
                )
            } else {
                ProfileHasNoFanfictions
            }
        }
    }
}
