package fr.ffnet.downloader.repository

import fr.ffnet.downloader.fanfictionutils.ProfileBuilder

class ProfileRepository(
    private val service: SearchService,
    private val profileDao: ProfileDao,
    private val profileBuilder: ProfileBuilder
) {
    fun loadProfileInfo(profileId: String): ProfileRepositoryResult {
        val response = service.getProfile(profileId).execute()
        return if (response.isSuccessful) {
            response.body()?.let { responseBody ->

                val profileInfo = profileBuilder.buildProfile(
                    profileId,
                    responseBody.string()
                )

                ProfileRepositoryResult.ProfileRepositoryResultSuccess(
                    profileId = profileId
                )
            } ?: ProfileRepositoryResult.ProfileRepositoryResultFailure
        } else {
            ProfileRepositoryResult.ProfileRepositoryResultFailure
        }

    }

    sealed class ProfileRepositoryResult {
        data class ProfileRepositoryResultSuccess(
            val profileId: String
        ) : ProfileRepositoryResult()

        object ProfileRepositoryResultFailure : ProfileRepositoryResult()
    }
}
