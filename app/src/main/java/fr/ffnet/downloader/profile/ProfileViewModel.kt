package fr.ffnet.downloader.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.BuildConfig
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import fr.ffnet.downloader.utils.DateFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val urlTransformer: UrlTransformer,
    private val dateFormatter: DateFormatter,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private lateinit var historyList: LiveData<List<ProfileUIModel>>
    private lateinit var isAssociatedLiveData: LiveData<Boolean>
    fun getIsAssociated(): LiveData<Boolean> = isAssociatedLiveData

    fun loadIsProfileAssociated() {
        isAssociatedLiveData = profileRepository.hasAssociatedProfile()
    }

    fun associateProfile(profileUrl: String?) {
        if (!profileUrl.isNullOrEmpty()) {
            when (val urlTransformationResult = urlTransformer.getProfileIdFromUrl(profileUrl)) {
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

    fun loadProfileHistory(): LiveData<List<ProfileUIModel>> {
        historyList = Transformations.map(profileRepository.loadHistory()) { historyList ->
            historyList.map {
                ProfileUIModel(
                    profileId = it.profileId,
                    name = it.name,
                    fetchedDate = dateFormatter.format(it.fetchedDate),
                    url = "${BuildConfig.API_BASE_URL}u/${it.profileId}"
                )
            }
        }
        return historyList
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

class ProfileViewModelFactory(
    private val creator: () -> ProfileViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}
