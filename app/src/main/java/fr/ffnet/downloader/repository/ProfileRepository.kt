package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fr.ffnet.downloader.repository.ProfileRepository.ProfileRepositoryResult.ProfileRepositoryResultFailure
import fr.ffnet.downloader.repository.ProfileRepository.ProfileRepositoryResult.ProfileRepositoryResultSuccess
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.repository.dao.ProfileDao
import fr.ffnet.downloader.repository.entities.Author
import fr.ffnet.downloader.repository.entities.AuthorEntity
import fr.ffnet.downloader.repository.entities.ProfileFanfictionEntity
import fr.ffnet.downloader.search.Fanfiction
import fr.ffnet.downloader.utils.FanfictionConverter
import fr.ffnet.downloader.utils.ProfileBuilder
import org.joda.time.LocalDateTime

class ProfileRepository(
    private val regularCrawlService: RegularCrawlService,
    private val profileDao: ProfileDao,
    private val fanfictionDao: FanfictionDao,
    private val profileBuilder: ProfileBuilder,
    private val fanfictionConverter: FanfictionConverter
) {

    companion object {
        const val PROFILE_TYPE_FAVORITE = 1
        const val PROFILE_TYPE_MY_STORY = 2
    }

    fun loadProfileInfo(authorId: String): ProfileRepositoryResult {
        val response = regularCrawlService.getProfile(authorId).execute()
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->

                val profileInfo = profileBuilder.buildProfile(authorId, responseBody.string())

                val favoriteIds = insertListAndReturnIds(profileInfo.favoriteFanfictionList)
                val storyIds = insertListAndReturnIds(profileInfo.myFanfictionList)

                profileDao.deleteProfileMapping(authorId)
                favoriteIds.map {
                    profileDao.insertProfileFanfiction(
                        ProfileFanfictionEntity(
                            profileId = authorId,
                            fanfictionId = it,
                            profileType = PROFILE_TYPE_FAVORITE
                        )
                    )
                }
                storyIds.map {
                    profileDao.insertProfileFanfiction(
                        ProfileFanfictionEntity(
                            profileId = authorId,
                            fanfictionId = it,
                            profileType = PROFILE_TYPE_MY_STORY
                        )
                    )
                }

                val author = profileDao.getAuthor(authorId)
                if (author == null) {
                    profileDao.insertAuthor(
                        AuthorEntity(
                            authorId = profileInfo.profileId,
                            name = profileInfo.name,
                            fetchedDate = LocalDateTime.now(),
                            nbstories = storyIds.size.toString(),
                            nbFavorites = favoriteIds.size.toString()
                        )
                    )
                } else {
                    profileDao.updateAuthor(
                        author.copy(
                            fetchedDate = LocalDateTime.now(),
                            nbstories = storyIds.size.toString(),
                            nbFavorites = favoriteIds.size.toString()
                        )
                    )
                }

                return ProfileRepositoryResultSuccess(
                    authorId = authorId,
                    authorName = profileInfo.name,
                    favoritesNb = favoriteIds.size,
                    storiesNb = storyIds.size,
                )
            }
        }
        return ProfileRepositoryResultFailure
    }

    fun loadSyncedAuthors(): LiveData<List<Author>> {
        return Transformations.map(profileDao.getSyncedAuthors()) { authorEntityList ->
            authorEntityList.map {
                Author(
                    id = it.authorId,
                    name = it.name,
                    nbStories = it.nbstories,
                    nbFavorites = it.nbFavorites,
                    fetchedDate = it.fetchedDate
                )
            }
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

    sealed class ProfileRepositoryResult {
        data class ProfileRepositoryResultSuccess(
            val authorId: String,
            val authorName: String,
            val favoritesNb: Int,
            val storiesNb: Int
        ) : ProfileRepositoryResult()

        object ProfileRepositoryResultFailure : ProfileRepositoryResult()
    }
}
