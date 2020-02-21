package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fr.ffnet.downloader.FanfictionConverter
import fr.ffnet.downloader.fanfictionutils.ProfileBuilder
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.repository.dao.ProfileDao
import fr.ffnet.downloader.repository.entities.ProfileEntity
import fr.ffnet.downloader.repository.entities.ProfileFanfictionEntity
import fr.ffnet.downloader.search.Fanfiction
import org.joda.time.LocalDateTime

class ProfileRepository(
    private val service: CrawlService,
    private val profileDao: ProfileDao,
    private val fanfictionDao: FanfictionDao,
    private val profileBuilder: ProfileBuilder,
    private val fanfictionConverter: FanfictionConverter
) {

    companion object {
        const val PROFILE_TYPE_FAVORITE = 1
        const val PROFILE_TYPE_MY_STORY = 2
    }

    fun loadProfileInfo(profileId: String): ProfileRepositoryResult {
        val response = service.getProfile(profileId).execute()
        return if (response.isSuccessful) {
            response.body()?.let { responseBody ->

                val profileInfo = profileBuilder.buildProfile(profileId, responseBody.string())

                val favoriteIds = insertListAndReturnIds(profileInfo.favoriteFanfictionList)
                val storyIds = insertListAndReturnIds(profileInfo.myFanfictionList)

                profileDao.deleteProfileMapping(profileId)
                favoriteIds.map {
                    profileDao.insertProfileFanfiction(
                        ProfileFanfictionEntity(
                            profileId = profileId,
                            fanfictionId = it,
                            profileType = PROFILE_TYPE_FAVORITE
                        )
                    )
                }
                storyIds.map {
                    profileDao.insertProfileFanfiction(
                        ProfileFanfictionEntity(
                            profileId = profileId,
                            fanfictionId = it,
                            profileType = PROFILE_TYPE_MY_STORY
                        )
                    )
                }

                val profile = profileDao.getProfile(profileId)
                if (profile == null) {
                    profileDao.insertProfile(
                        ProfileEntity(
                            profileId = profileInfo.profileId,
                            name = profileInfo.name,
                            fetchedDate = LocalDateTime.now(),
                            isAssociated = true
                        )
                    )
                } else {
                    profileDao.associateProfile(profileId)
                }

                ProfileRepositoryResult.ProfileRepositoryResultSuccess(
                    profileId = profileId
                )
            } ?: ProfileRepositoryResult.ProfileRepositoryResultFailure
        } else {
            ProfileRepositoryResult.ProfileRepositoryResultFailure
        }
    }

    fun hasAssociatedProfile(): LiveData<String?> = Transformations.map(profileDao.getProfile()) {
        it?.name
    }

    fun dissociateProfile() {
        profileDao.dissociateProfile()
    }

    fun refreshProfile() {
        profileDao.getAssociatedProfile()?.let { profileId ->
            loadProfileInfo(profileId)
        }
    }

    private fun insertListAndReturnIds(fanfictionList: List<Fanfiction>): List<String> {
        return fanfictionList.map { fanfiction ->
            val fanfictionInfo = fanfictionDao.getFanfiction(fanfiction.id)
            if (fanfictionInfo == null) {
                val fanfictionEntity = fanfictionConverter.toFanfictionEntity(fanfiction)
                fanfictionDao.insertFanfiction(fanfictionEntity)
            }
            fanfiction.id
        }
    }

    fun loadHistory(): LiveData<List<ProfileEntity>> = profileDao.getProfileHistory()

    sealed class ProfileRepositoryResult {
        data class ProfileRepositoryResultSuccess(val profileId: String) : ProfileRepositoryResult()
        object ProfileRepositoryResultFailure : ProfileRepositoryResult()
    }
}
