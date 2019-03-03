package fr.ffnet.downloader.profile

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProfileViewModel(
    private val urlTransformer: UrlTransformer,
    private val resources: Resources,
    private val sharedPreferences: SharedPreferences,
    private val databaseRepository: DatabaseRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private lateinit var profileId: String
    private lateinit var fanfictionResult: LiveData<ProfileFanfictionsResult>
    fun getFanfictionList(): LiveData<ProfileFanfictionsResult> = fanfictionResult

    private val isProfileAssociated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    companion object {
        private const val ASSOCIATED_PROFILE = "ASSOCIATED_PROFILE"
    }

    init {
        setIsProfileAssociated()
    }

    fun getIsProfileAssociated(): LiveData<Boolean> = isProfileAssociated

    fun loadFanfictionsFromProfile() {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        fanfictionResult = Transformations.map(
            databaseRepository.getFanfictionsFromProfile(profileId)
        ) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                ProfileFanfictionsResult.ProfileHasFanfictions(fanfictionList.map { fanfiction ->
                    FanfictionSyncedUIModel(
                        id = fanfiction.id,
                        title = fanfiction.title,
                        updatedDate = formatter.format(fanfiction.updatedDate),
                        publishedDate = formatter.format(fanfiction.publishedDate),
                        syncedDate = fanfiction.syncedDate?.let {
                            formatter.format(fanfiction.syncedDate)
                        } ?: resources.getString(R.string.download_info_chapter_status_not_synced),
                        chapters = resources.getString(
                            R.string.synced_fanfictions_chapters,
                            fanfiction.nbSyncedChapters,
                            fanfiction.nbChapters
                        )
                    )
                })
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

    private fun loadProfileInfo(profileId: String) {
        this.profileId = profileId
        CoroutineScope(Dispatchers.IO).launch {
            profileRepository.loadProfileInfo(profileId)
        }
    }

    private fun setIsProfileAssociated() {
        CoroutineScope(Dispatchers.IO).launch {
            val hasAssociatedProfile = profileRepository.hasAssociatedProfile()
            CoroutineScope(Dispatchers.Main).launch {
                isProfileAssociated.value = hasAssociatedProfile
            }
        }
    }

    sealed class ProfileFanfictionsResult {
        data class ProfileHasFanfictions(val fanfictionList: List<FanfictionSyncedUIModel>) : ProfileFanfictionsResult()
        object ProfileHasNoFanfictions : ProfileFanfictionsResult()
    }
}
