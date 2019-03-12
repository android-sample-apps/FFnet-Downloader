package fr.ffnet.downloader.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val urlTransformer: UrlTransformer,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private lateinit var profileId: String

    private lateinit var isAssociatedLiveData: LiveData<Boolean>
    fun getIsAssociated(): LiveData<Boolean> = isAssociatedLiveData

    fun loadIsProfileAssociated() {
        isAssociatedLiveData = profileRepository.hasAssociatedProfile()
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

    fun dissociateProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            profileRepository.dissociateProfile()
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
