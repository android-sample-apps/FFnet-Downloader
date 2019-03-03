package fr.ffnet.downloader.profile

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val urlTransformer: UrlTransformer,
    private val resources: Resources,
    private val sharedPreferences: SharedPreferences,
    private val databaseRepository: DatabaseRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

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

    fun associateProfile(profileUrl: String?) {
        if (!profileUrl.isNullOrEmpty()) {
            val urlTransformationResult = urlTransformer.getFanfictionIdFromUrl(profileUrl)
            when (urlTransformationResult) {
                is UrlTransformer.UrlTransformationResult.UrlTransformSuccess -> loadProfileInfo(
                    urlTransformationResult.id
                )
                is UrlTransformer.UrlTransformationResult.UrlTransformFailure -> TODO()
            }
        }
    }

    private fun loadProfileInfo(profileId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            profileRepository.loadProfileInfo(profileId)
        }
    }

    private fun setIsProfileAssociated() {
        isProfileAssociated.value = sharedPreferences.getString(ASSOCIATED_PROFILE, null)?.let {
            true
        } ?: false
    }
}
