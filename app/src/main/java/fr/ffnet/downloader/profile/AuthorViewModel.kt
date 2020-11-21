package fr.ffnet.downloader.profile

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.R
import fr.ffnet.downloader.profile.AuthorUIItem.SearchAuthorNotResultUIItem
import fr.ffnet.downloader.profile.AuthorUIItem.SyncedAuthorUIItem
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.repository.ProfileRepository.ProfileRepositoryResult.ProfileRepositoryResultFailure
import fr.ffnet.downloader.repository.ProfileRepository.ProfileRepositoryResult.ProfileRepositoryResultSuccess
import fr.ffnet.downloader.repository.SearchRepository
import fr.ffnet.downloader.utils.SingleLiveEvent
import fr.ffnet.downloader.utils.UIBuilder
import fr.ffnet.downloader.utils.UrlTransformer
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthorViewModel(
    private val resources: Resources,
    private val uiBuilder: UIBuilder,
    private val urlTransformer: UrlTransformer,
    private val searchRepository: SearchRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {


    val navigateToAuthor: SingleLiveEvent<AuthorLoaded> = SingleLiveEvent()
    val error: SingleLiveEvent<String> = SingleLiveEvent()
    var authorRefreshResult: SingleLiveEvent<AuthorRefreshResult> = SingleLiveEvent()

    val authorResult: MediatorLiveData<List<AuthorUIItem>> by lazy {
        MediatorLiveData<List<AuthorUIItem>>()
    }

    private lateinit var syncedResult: LiveData<List<AuthorUIItem>>
    private var searchResult: MutableLiveData<List<AuthorUIItem>> = MutableLiveData()

    fun emptySearchResults() {
        searchResult.postValue(emptyList())
    }

    fun unsyncAuthor(author: SyncedAuthorUIItem) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.unsyncAuthor(author.id)
        }
    }

    fun searchAuthor(searchText: String?) {
        when (val urlTransformationResult = urlTransformer.getProfileIdFromUrl(searchText)) {
            is UrlTransformSuccess -> loadAuthorInfo(urlTransformationResult.id)
            is UrlTransformFailure -> {
                if (searchText != null) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val searchList = searchRepository.searchAuthor(
                            searchText.split(" ")
                        )
                        val title = AuthorUIItem.AuthorTitleUIItem(
                            title = resources.getString(R.string.search_result_title)
                        )
                        if (searchList.isNotEmpty()) {
                            val authorResult = searchList.map {
                                uiBuilder.buildSearchAuthorUI(it)
                            }
                            searchResult.postValue(listOf(title).plus(authorResult))
                        } else {
                            searchResult.postValue(
                                listOf(
                                    title,
                                    SearchAuthorNotResultUIItem(resources.getString(R.string.search_no_result))
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadAuthorInfo(authorId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val profileResult = profileRepository.loadProfileInfo(authorId)) {
                is ProfileRepositoryResultSuccess -> navigateToAuthor.postValue(
                    AuthorLoaded(
                        authorId = profileResult.authorId,
                        authorName = profileResult.authorName,
                        shouldShowStoriesFirst = profileResult.storiesNb > 0
                    )
                )
                ProfileRepositoryResultFailure -> resources.getString(R.string.author_load_error)
            }
        }
    }

    fun refreshAuthorsInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            syncedResult.value?.filterIsInstance<SyncedAuthorUIItem>()?.let { syncedAuthorList ->
                val allRefreshResult = syncedAuthorList.map {
                    profileRepository.loadProfileInfo(it.id)
                }
                if (allRefreshResult.all { it is ProfileRepositoryResultSuccess }) {
                    authorRefreshResult.postValue(AuthorRefreshResult.Refreshed)
                } else {
                    authorRefreshResult.postValue(
                        AuthorRefreshResult.NotRefreshed(
                            resources.getString(R.string.author_refresh_error)
                        )
                    )
                }
            }
        }
    }

    fun loadSearchAndSynced() {
        loadSyncedAuthors()
        authorResult.removeSource(syncedResult)
        authorResult.removeSource(searchResult)
        authorResult.apply {
            addSource(searchResult) {
                authorResult.value = combineLatestData(
                    searchResult = searchResult.value ?: emptyList(),
                    syncedResult = syncedResult.value ?: emptyList()
                )
            }
            addSource(syncedResult) {
                authorResult.value = combineLatestData(
                    searchResult = searchResult.value ?: emptyList(),
                    syncedResult = syncedResult.value ?: emptyList()
                )
            }
        }
    }

    private fun combineLatestData(
        searchResult: List<AuthorUIItem>,
        syncedResult: List<AuthorUIItem>
    ): List<AuthorUIItem> {
        return searchResult.plus(syncedResult)
    }

    private fun loadSyncedAuthors() {
        syncedResult = Transformations.map(profileRepository.loadSyncedAuthors()) { authorList ->
            if (authorList.isNotEmpty()) {
                val title = AuthorUIItem.AuthorTitleUIItem(
                    title = resources.getString(R.string.synced_author_title)
                )
                val syncedAuthorList = authorList.map {
                    uiBuilder.buildSyncedAuthor(it)
                }
                listOf(title).plus(syncedAuthorList)
            } else emptyList()
        }
    }

    data class AuthorLoaded(
        val authorId: String,
        val authorName: String,
        val shouldShowStoriesFirst: Boolean
    )

    sealed class AuthorRefreshResult {
        object Refreshed : AuthorRefreshResult()
        data class NotRefreshed(val message: String) : AuthorRefreshResult()
    }
}
