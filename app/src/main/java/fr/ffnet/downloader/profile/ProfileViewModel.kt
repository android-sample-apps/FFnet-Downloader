package fr.ffnet.downloader.profile

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.search.Fanfiction
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import fr.ffnet.downloader.utils.LiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProfileViewModel(
    private val urlTransformer: UrlTransformer,
    private val resources: Resources,
    private val databaseRepository: DatabaseRepository,
    private val profileRepository: ProfileRepository,
    private val downloaderRepository: DownloaderRepository
) : ViewModel() {

    private lateinit var profileId: String
    private lateinit var myFavoritesResult: LiveData<ProfileFanfictionsResult>
    fun getMyFavoritesList(): LiveData<ProfileFanfictionsResult> = myFavoritesResult

    private lateinit var myStoriesResult: LiveData<ProfileFanfictionsResult>
    fun getMyStoriesResult(): LiveData<ProfileFanfictionsResult> = myStoriesResult

    private lateinit var isAssociatedLiveData: LiveData<Boolean>
    fun getIsAssociated(): LiveData<Boolean> = isAssociatedLiveData

    private val navigateToFanfictionActivity = MutableLiveData<LiveEvent<String>>()
    val navigateToFanfiction: LiveData<LiveEvent<String>>
        get() = navigateToFanfictionActivity

    fun loadIsProfileAssociated() {
        isAssociatedLiveData = profileRepository.hasAssociatedProfile()
    }

    fun loadFavoriteFanfictions() {
        myFavoritesResult = Transformations.map(
            databaseRepository.getMyFavoriteFanfictions()
        ) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                ProfileFanfictionsResult.ProfileHasFanfictions(
                    buildFanfictionSyncedUIModelList(fanfictionList)
                )
            } else {
                ProfileFanfictionsResult.ProfileHasNoFanfictions
            }
        }
    }

    fun loadMyFanfictions() {
        myStoriesResult = Transformations.map(
            databaseRepository.getMyFanfictions()
        ) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                ProfileFanfictionsResult.ProfileHasFanfictions(
                    buildFanfictionSyncedUIModelList(fanfictionList)
                )
            } else {
                ProfileFanfictionsResult.ProfileHasNoFanfictions
            }
        }
    }

    fun associateProfile(profileUrl: String?) {
        if (!profileUrl.isNullOrEmpty()) {
            val urlTransformationResult = urlTransformer.getProfileIdFromUrl(profileUrl)
            when (urlTransformationResult) {
                is UrlTransformer.UrlTransformationResult.UrlTransformSuccess -> loadProfileInfo(
                    urlTransformationResult.id
                )
                is UrlTransformer.UrlTransformationResult.UrlTransformFailure -> {
                    // Do nothing
                }
            }
        }
    }

    fun loadFanfictionInfo(fanfictionId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val fanfictionResult = downloaderRepository.loadFanfictionInfo(fanfictionId)
            if (fanfictionResult is DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess) {
                CoroutineScope(Dispatchers.Main).launch {
                    navigateToFanfictionActivity.value = LiveEvent(fanfictionId)
                }
            }
        }
    }

    fun dissociateProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            profileRepository.dissociateProfile()
        }
    }

    private fun buildFanfictionSyncedUIModelList(fanfictionList: List<Fanfiction>): List<FanfictionSyncedUIModel> {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return fanfictionList.map { fanfiction ->
            FanfictionSyncedUIModel(
                id = fanfiction.id,
                title = fanfiction.title,
                updatedDate = formatter.format(fanfiction.updatedDate),
                publishedDate = formatter.format(fanfiction.publishedDate),
                fetchedDate = fanfiction.fetchedDate?.toString("yyyy-MM-dd HH:mm")
                    ?: resources.getString(R.string.download_info_chapter_status_not_synced),
                chapters = resources.getString(
                    R.string.synced_fanfictions_chapters,
                    fanfiction.nbSyncedChapters,
                    fanfiction.nbChapters
                )
            )
        }
    }

    private fun loadProfileInfo(profileId: String) {
        this.profileId = profileId
        CoroutineScope(Dispatchers.IO).launch {
            profileRepository.loadProfileInfo(profileId)
        }
    }

    sealed class ProfileFanfictionsResult {
        data class ProfileHasFanfictions(
            val fanfictionList: List<FanfictionSyncedUIModel>
        ) : ProfileFanfictionsResult()

        object ProfileHasNoFanfictions : ProfileFanfictionsResult()
    }
}
