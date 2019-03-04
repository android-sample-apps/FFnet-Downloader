package fr.ffnet.downloader.repository

import fr.ffnet.downloader.fanfictionutils.FanfictionTransformer
import fr.ffnet.downloader.fanfictionutils.ProfileBuilder
import fr.ffnet.downloader.search.Fanfiction

class ProfileRepository(
    private val service: SearchService,
    private val profileDao: ProfileDao,
    private val fanfictionDao: FanfictionDao,
    private val profileBuilder: ProfileBuilder,
    private val fanfictionTransformer: FanfictionTransformer
) {
    fun loadProfileInfo(profileId: String): ProfileRepositoryResult {
        val response = service.getProfile(profileId).execute()
        return if (response.isSuccessful) {
            response.body()?.let { responseBody ->

                val profileInfo = profileBuilder.buildProfile(profileId, responseBody.string())

                val favoriteIds = insertListAndReturnIds(profileInfo.favoriteFanfictionList)
                val storyIds = insertListAndReturnIds(profileInfo.myFanfictionList)

                profileDao.deleteProfileMapping(profileId)
                favoriteIds.map {
                    profileDao.insertProfileFanfiction(ProfileFanfictionEntity(
                        profileId = profileId,
                        fanfictionId = it,
                        isFavorite = true
                    ))
                }
                storyIds.map {
                    profileDao.insertProfileFanfiction(ProfileFanfictionEntity(
                        profileId = profileId,
                        fanfictionId = it,
                        isFavorite = false
                    ))
                }

                val profile = profileDao.getProfile(profileId)
                if (profile == null) {
                    profileDao.insertProfile(
                        ProfileEntity(
                            profileId = profileInfo.profileId,
                            name = profileInfo.name,
                            isAssociated = true
                        )
                    )
                }

                ProfileRepositoryResult.ProfileRepositoryResultSuccess(
                    profileId = profileId
                )
            } ?: ProfileRepositoryResult.ProfileRepositoryResultFailure
        } else {
            ProfileRepositoryResult.ProfileRepositoryResultFailure
        }
    }

    fun hasAssociatedProfile(): Boolean = profileDao.getProfile().isNotEmpty()

    private fun insertListAndReturnIds(fanfictionList: List<Fanfiction>): List<String> {
        return fanfictionList.map { fanfiction ->
            val fanfictionInfo = fanfictionDao.getFanfiction(fanfiction.id)
            if (fanfictionInfo == null) {
                val fanfictionEntity = fanfictionTransformer.toFanfictionEntity(fanfiction)
                fanfictionDao.insertFanfiction(fanfictionEntity)
            }
            fanfiction.id
        }
    }

    sealed class ProfileRepositoryResult {
        data class ProfileRepositoryResultSuccess(
            val profileId: String
        ) : ProfileRepositoryResult()

        object ProfileRepositoryResultFailure : ProfileRepositoryResult()
    }
}
