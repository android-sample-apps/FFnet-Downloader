package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fr.ffnet.downloader.repository.AuthorRepository.AuthorRepositoryResult.AuthorRepositoryResultFailure
import fr.ffnet.downloader.repository.AuthorRepository.AuthorRepositoryResult.AuthorRepositoryResultSuccess
import fr.ffnet.downloader.repository.dao.AuthorDao
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.repository.entities.Author
import fr.ffnet.downloader.repository.entities.AuthorEntity
import fr.ffnet.downloader.repository.entities.ProfileFanfictionEntity
import fr.ffnet.downloader.search.Fanfiction
import fr.ffnet.downloader.utils.FanfictionConverter
import fr.ffnet.downloader.utils.ProfileBuilder
import org.joda.time.LocalDateTime

class AuthorRepository(
    private val regularCrawlService: RegularCrawlService,
    private val dao: AuthorDao,
    private val fanfictionDao: FanfictionDao,
    private val profileBuilder: ProfileBuilder,
    private val fanfictionConverter: FanfictionConverter
) {

    companion object {
        const val PROFILE_TYPE_FAVORITE = 1
        const val PROFILE_TYPE_MY_STORY = 2
    }

    fun unsyncAuthor(authorId: String) {
        dao.deleteProfileMapping(authorId)
        dao.deleteAuthor(authorId)
    }

    fun loadProfileInfo(authorId: String): AuthorRepositoryResult {
        val response = regularCrawlService.getProfile(authorId).execute()
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->

                val profileInfo = profileBuilder.buildProfile(authorId, responseBody.string())

                val favoriteIds = insertListAndReturnIds(profileInfo.favoriteFanfictionList)
                val storyIds = insertListAndReturnIds(profileInfo.myFanfictionList)

                dao.deleteProfileMapping(authorId)
                favoriteIds.map {
                    dao.insertProfileFanfiction(
                        ProfileFanfictionEntity(
                            profileId = authorId,
                            fanfictionId = it,
                            profileType = PROFILE_TYPE_FAVORITE
                        )
                    )
                }
                storyIds.map {
                    dao.insertProfileFanfiction(
                        ProfileFanfictionEntity(
                            profileId = authorId,
                            fanfictionId = it,
                            profileType = PROFILE_TYPE_MY_STORY
                        )
                    )
                }

                val author = dao.getAuthor(authorId)
                if (author == null) {
                    dao.insertAuthor(
                        AuthorEntity(
                            authorId = profileInfo.profileId,
                            name = profileInfo.name,
                            fetchedDate = LocalDateTime.now(),
                            nbstories = storyIds.size.toString(),
                            nbFavorites = favoriteIds.size.toString()
                        )
                    )
                } else {
                    dao.updateAuthor(
                        author.copy(
                            fetchedDate = LocalDateTime.now(),
                            nbstories = storyIds.size.toString(),
                            nbFavorites = favoriteIds.size.toString()
                        )
                    )
                }

                return AuthorRepositoryResultSuccess(
                    authorId = authorId,
                    authorName = profileInfo.name,
                    favoritesNb = favoriteIds.size,
                    storiesNb = storyIds.size,
                )
            }
        }
        return AuthorRepositoryResultFailure
    }

    fun loadSyncedAuthors(): LiveData<List<Author>> {
        return Transformations.map(dao.getSyncedAuthors()) { authorEntityList ->
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

    fun getAuthor(authorId: String): AuthorRepositoryResult {
        return dao.getAuthor(authorId)?.let {
            AuthorRepositoryResultSuccess(
                authorId = it.authorId,
                authorName = it.name,
                storiesNb = it.nbstories.toInt(),
                favoritesNb = it.nbFavorites.toInt()
            )
        } ?: AuthorRepositoryResultFailure
    }

    sealed class AuthorRepositoryResult {
        data class AuthorRepositoryResultSuccess(
            val authorId: String,
            val authorName: String,
            val favoritesNb: Int,
            val storiesNb: Int
        ) : AuthorRepositoryResult()

        object AuthorRepositoryResultFailure : AuthorRepositoryResult()
    }
}
