package fr.ffnet.downloader.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val urlTransformer: UrlTransformer,
    private val profileRepository: ProfileRepository
) : ViewModel() {

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

    fun refreshProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.refreshProfile()
        }
    }

    fun dissociateProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.dissociateProfile()
        }
    }

    private fun loadProfileInfo(profileId: String) {
        viewModelScope.launch(Dispatchers.IO) {
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
