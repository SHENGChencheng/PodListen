package com.podlisten.android.core.data.di

import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import com.podlisten.android.core.data.Dispatcher
import com.podlisten.android.core.data.PodListenDispatcher
import com.podlisten.android.core.data.database.PodListenDatabase
import com.podlisten.android.core.data.database.dao.CategoriesDao
import com.podlisten.android.core.data.database.dao.EpisodesDao
import com.podlisten.android.core.data.database.dao.PodcastCategoryEntryDao
import com.podlisten.android.core.data.database.dao.PodcastFollowedEntryDao
import com.podlisten.android.core.data.database.dao.PodcastsDao
import com.podlisten.android.core.data.database.dao.TransactionRunner
import com.podlisten.android.core.data.repository.CategoryStore
import com.podlisten.android.core.data.repository.EpisodeStore
import com.podlisten.android.core.data.repository.LocalCategoryStore
import com.podlisten.android.core.data.repository.LocalEpisodeStore
import com.podlisten.android.core.data.repository.LocalPodcastStore
import com.podlisten.android.core.data.repository.PodcastStore
import com.podlisten.android.core.domain.player.EpisodePlayer
import com.podlisten.android.core.domain.player.MockEpisodePlayer
import com.rometools.rome.io.SyndFeedInput
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataDiModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(File(context.cacheDir, "http_cache"), (20 * 1024 * 1024).toLong()))
        // TODO: BuildConfig.DEBUG
//        .apply {
//            if (BuildConfig.DEBUG) eventListenerFactory(LoggingEventListener.Factory())
//        }
        .build()

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PodListenDatabase =
        Room.databaseBuilder(context, PodListenDatabase::class.java, "data.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader = ImageLoader.Builder(context)
        .respectCacheHeaders(false)
        .build()

    @Provides
    @Singleton
    fun provideCategoriesDao(
        database: PodListenDatabase
    ): CategoriesDao = database.categoriesDao()

    @Provides
    @Singleton
    fun providePodcastCategoryEntryDao(
        database: PodListenDatabase
    ): PodcastCategoryEntryDao = database.podcastCategoryEntryDao()

    @Provides
    @Singleton
    fun providePodcastsDao(
        database: PodListenDatabase
    ): PodcastsDao = database.podcastsDao()

    @Provides
    @Singleton
    fun provideEpisodesDao(
        database: PodListenDatabase
    ): EpisodesDao = database.episodesDao()

    @Provides
    @Singleton
    fun providePodcastFollowedEntryDao(
        database: PodListenDatabase
    ): PodcastFollowedEntryDao = database.podcastFollowedEntryDao()

    @Provides
    @Singleton
    fun provideTransactionRunner(
        database: PodListenDatabase
    ): TransactionRunner = database.transactionRunnerDao()

    @Provides
    @Singleton
    fun provideSyndFeedInput() = SyndFeedInput()

    @Provides
    @Dispatcher(PodListenDispatcher.IO)
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(PodListenDispatcher.Main)
    @Singleton
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    fun provideEpisodeStore(
        episodeDao: EpisodesDao
    ): EpisodeStore = LocalEpisodeStore(episodeDao)

    @Provides
    @Singleton
    fun providePodcastStore(
        podcastDao: PodcastsDao,
        podcastFollowedEntryDao: PodcastFollowedEntryDao,
        transactionRunner: TransactionRunner,
    ): PodcastStore = LocalPodcastStore(
        podcastDao = podcastDao,
        podcastFollowedEntryDao = podcastFollowedEntryDao,
        transactionRunner = transactionRunner
    )

    @Provides
    @Singleton
    fun provideCategoryStore(
        categoriesDao: CategoriesDao,
        podcastCategoryEntryDao: PodcastCategoryEntryDao,
        podcastDao: PodcastsDao,
        episodeDao: EpisodesDao,
    ): CategoryStore = LocalCategoryStore(
        episodesDao = episodeDao,
        podcastsDao = podcastDao,
        categoriesDao = categoriesDao,
        categoryEntryDao = podcastCategoryEntryDao,
    )

    @Provides
    @Singleton
    fun provideEpisodePlayer(
        @Dispatcher(PodListenDispatcher.Main) mainDispatcher: CoroutineDispatcher
    ): EpisodePlayer = MockEpisodePlayer(mainDispatcher)
}