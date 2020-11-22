package fr.ffnet.downloader.profile

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.profile.AuthorUIItem.SearchAuthorNotResultUIItem
import fr.ffnet.downloader.profile.AuthorUIItem.SyncedAuthorUIItem
import fr.ffnet.downloader.repository.AuthorRepository
import fr.ffnet.downloader.repository.AuthorRepository.AuthorRepositoryResult.AuthorRepositoryResultFailure
import fr.ffnet.downloader.repository.AuthorRepository.AuthorRepositoryResult.AuthorRepositoryResultSuccess
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
    private val authorRepository: AuthorRepository
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
            authorRepository.unsyncAuthor(author.id)
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
            when (val author = authorRepository.getAuthor(authorId)) {
                is AuthorRepositoryResultSuccess -> {
                    navigateToAuthor(author)
                    authorRepository.loadProfileInfo(authorId)
                }
                AuthorRepositoryResultFailure -> {
                    when (val profileResult = authorRepository.loadProfileInfo(authorId)) {
                        is AuthorRepositoryResultSuccess -> navigateToAuthor(profileResult)
                        AuthorRepositoryResultFailure -> displayErrorMessage(R.string.author_load_error)
                    }
                }
            }
        }
    }

    fun refreshAuthorsInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            syncedResult.value?.filterIsInstance<SyncedAuthorUIItem>()?.let { syncedAuthorList ->
                val allRefreshResult = syncedAuthorList.map {
                    authorRepository.loadProfileInfo(it.id)
                }
                if (allRefreshResult.all { it is AuthorRepositoryResultSuccess }) {
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

    private fun navigateToAuthor(author: AuthorRepositoryResultSuccess) {
        navigateToAuthor.postValue(
            AuthorLoaded(
                authorId = author.authorId,
                authorName = author.authorName,
                shouldShowStoriesFirst = author.storiesNb > 0
            )
        )
    }

    private fun combineLatestData(
        searchResult: List<AuthorUIItem>,
        syncedResult: List<AuthorUIItem>
    ): List<AuthorUIItem> {
        return searchResult.plus(syncedResult)
    }

    private fun loadSyncedAuthors() {
        syncedResult = Transformations.map(authorRepository.loadSyncedAuthors()) { authorList ->
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

    private fun displayErrorMessage(messageResource: Int) {
        val errorMessage = resources.getString(messageResource)
        FFLogger.d(FFLogger.EVENT_KEY, errorMessage)
        error.postValue(errorMessage)
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
