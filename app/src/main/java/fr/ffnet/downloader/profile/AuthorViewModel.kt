package fr.ffnet.downloader.profile

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.R
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.repository.ProfileRepository.ProfileRepositoryResult
import fr.ffnet.downloader.repository.ProfileRepository.ProfileRepositoryResult.*
import fr.ffnet.downloader.repository.SearchRepository
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

    val authorResult: MediatorLiveData<List<AuthorUIItem>> by lazy {
        MediatorLiveData<List<AuthorUIItem>>()
    }

    private lateinit var syncedResult: LiveData<List<AuthorUIItem>>
    private var searchResult: MutableLiveData<List<AuthorUIItem>> = MutableLiveData()

    fun emptySearchResults() {
        searchResult.postValue(emptyList())
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
                        if (searchList.isNotEmpty()) {
                            val title = AuthorUIItem.AuthorTitleUIItem(
                                title = resources.getString(R.string.search_result_title)
                            )
                            val authorResult = searchList.map {
                                uiBuilder.buildSearchAuthorUI(it)
                            }
                            searchResult.postValue(listOf(title).plus(authorResult))
                        } else {
                            searchResult.postValue(emptyList())
                        }
                    }
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

    fun loadAuthorInfo(authorId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val profileResult = profileRepository.loadProfileInfo(authorId)
            when (profileResult) {
                is ProfileRepositoryResultSuccess -> TODO()
                ProfileRepositoryResultFailure -> TODO()
            }
        }
    }
}
