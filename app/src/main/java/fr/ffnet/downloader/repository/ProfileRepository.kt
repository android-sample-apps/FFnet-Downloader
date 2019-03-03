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

                val profile = profileDao.getProfile(profileId)
                if (profile == null) {
                    profileDao.insertProfile(
                        ProfileEntity(
                            profileId = profileInfo.profileId,
                            name = profileInfo.name,
                            myFavoritesList = favoriteIds.joinToString(),
                            myStoriesList = storyIds.joinToString()
                        )
                    )
                } else {
                    profileDao.updateProfile(
                        profileId,
                        favoriteIds.joinToString(),
                        storyIds.joinToString()
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
