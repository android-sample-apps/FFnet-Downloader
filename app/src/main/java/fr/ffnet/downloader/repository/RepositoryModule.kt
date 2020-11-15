package fr.ffnet.downloader.repository

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.repository.dao.ProfileDao
import fr.ffnet.downloader.utils.FanfictionBuilder
import fr.ffnet.downloader.utils.FanfictionConverter
import fr.ffnet.downloader.utils.ProfileBuilder
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    fun provideFanfictionDao(database: FanfictionDownloaderDatabase): FanfictionDao =
        database.fanfictionDao()

    @Provides
    fun provideProfileDao(database: FanfictionDownloaderDatabase): ProfileDao =
        database.profileDao()

    @Provides
    fun provideCrawlService(retrofit: Retrofit): CrawlService = retrofit.create(
        CrawlService::class.java
    )

    @Provides
    fun provideDatabaseRepository(
        dao: FanfictionDao,
        fanfictionConverter: FanfictionConverter
    ): DatabaseRepository = DatabaseRepository(dao, fanfictionConverter)

    @Provides
    @Singleton
    fun provideDownloaderRepository(
        service: CrawlService,
        fanfictionDao: FanfictionDao,
        fanfictionBuilder: FanfictionBuilder,
        fanfictionConverter: FanfictionConverter,
        scheduler: WorkScheduler
    ): DownloaderRepository {
        return DownloaderRepository(
            service,
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
        service: CrawlService,
        profileDao: ProfileDao,
        profileBuilder: ProfileBuilder,
        fanfictionConverter: FanfictionConverter,
        fanfictionDao: FanfictionDao
    ): ProfileRepository = ProfileRepository(
        service,
        profileDao,
        fanfictionDao,
        profileBuilder,
        fanfictionConverter
    )
}
