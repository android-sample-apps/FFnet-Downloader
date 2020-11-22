package fr.ffnet.downloader.repository

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import fr.ffnet.downloader.common.NetworkModule.Companion.MOBILE_WEBSITE
import fr.ffnet.downloader.common.NetworkModule.Companion.REGULAR_WEBSITE
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.repository.dao.AuthorDao
import fr.ffnet.downloader.utils.FanfictionBuilder
import fr.ffnet.downloader.utils.FanfictionConverter
import fr.ffnet.downloader.utils.ProfileBuilder
import fr.ffnet.downloader.utils.SearchBuilder
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    fun provideFanfictionDao(database: FanfictionDownloaderDatabase): FanfictionDao =
        database.fanfictionDao()

    @Provides
    fun provideProfileDao(database: FanfictionDownloaderDatabase): AuthorDao =
        database.authorDao()

    @Provides
    fun provideMobileCrawlService(
        @Named(MOBILE_WEBSITE) retrofit: Retrofit
    ): MobileCrawlService = retrofit.create(MobileCrawlService::class.java)

    @Provides
    fun provideRegularCrawlService(
        @Named(REGULAR_WEBSITE) retrofit: Retrofit
    ): RegularCrawlService = retrofit.create(RegularCrawlService::class.java)

    @Provides
    fun provideDatabaseRepository(
        dao: FanfictionDao,
        fanfictionConverter: FanfictionConverter
    ): DatabaseRepository = DatabaseRepository(dao, fanfictionConverter)

    @Provides
    @Singleton
    fun provideDownloaderRepository(
        regularCrawlService: RegularCrawlService,
        fanfictionDao: FanfictionDao,
        fanfictionBuilder: FanfictionBuilder,
        fanfictionConverter: FanfictionConverter,
        scheduler: WorkScheduler
    ): DownloaderRepository {
        return DownloaderRepository(
            regularCrawlService,
            fanfictionBuilder,
            fanfictionDao,
            fanfictionConverter,
            scheduler
        )
    }

    @Provides
    fun provideWorkScheduler(workManager: WorkManager): WorkScheduler {
        return WorkScheduler(workManager)
    }

    @Provides
    fun provideWorkManager(context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    fun provideProfileRepository(
        regularCrawlService: RegularCrawlService,
        profileDao: AuthorDao,
        profileBuilder: ProfileBuilder,
        fanfictionConverter: FanfictionConverter,
        fanfictionDao: FanfictionDao
    ): AuthorRepository = AuthorRepository(
        regularCrawlService,
        profileDao,
        fanfictionDao,
        profileBuilder,
        fanfictionConverter
    )

    @Provides
    fun provideSearchRepository(
        mobileServiceRegular: MobileCrawlService,
        searchBuilder: SearchBuilder
    ): SearchRepository = SearchRepository(
        mobileServiceRegular,
        searchBuilder
    )
}
