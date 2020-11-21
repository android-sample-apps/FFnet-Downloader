package fr.ffnet.downloader.profile

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.repository.SearchRepository
import fr.ffnet.downloader.utils.UIBuilder
import fr.ffnet.downloader.utils.UrlTransformer
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class AuthorModule(private val fragment: AuthorFragment) {

    @Provides
    fun provideAuthorViewModel(
        resources: Resources,
        uiBuilder: UIBuilder,
        urlTransformer: UrlTransformer,
        searchRepository: SearchRepository,
        profileRepository: ProfileRepository
    ): AuthorViewModel {
        val factory = ViewModelFactory {
            AuthorViewModel(
                resources,
                uiBuilder,
                urlTransformer,
                searchRepository,
                profileRepository
            )
        }
        return ViewModelProvider(fragment, factory)[AuthorViewModel::class.java]
    }
}
