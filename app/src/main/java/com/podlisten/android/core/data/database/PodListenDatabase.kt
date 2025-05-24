package com.podlisten.android.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.podlisten.android.core.data.database.dao.CategoriesDao
import com.podlisten.android.core.data.database.dao.EpisodesDao
import com.podlisten.android.core.data.database.dao.PodcastCategoryEntryDao
import com.podlisten.android.core.data.database.dao.PodcastFollowedEntryDao
import com.podlisten.android.core.data.database.dao.PodcastsDao
import com.podlisten.android.core.data.database.dao.TransactionRunnerDao
import com.podlisten.android.core.data.database.model.Category
import com.podlisten.android.core.data.database.model.Episode
import com.podlisten.android.core.data.database.model.Podcast
import com.podlisten.android.core.data.database.model.PodcastCategoryEntry
import com.podlisten.android.core.data.database.model.PodcastFollowedEntry

@Database(
    entities = [
        Podcast::class,
        Episode::class,
        PodcastCategoryEntry::class,
        Category::class,
        PodcastFollowedEntry::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeTypeConverters::class)
abstract class PodListenDatabase : RoomDatabase() {
    abstract fun podcastsDao(): PodcastsDao
    abstract fun episodesDao(): EpisodesDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun podcastCategoryEntryDao(): PodcastCategoryEntryDao
    abstract fun podcastFollowedEntryDao(): PodcastFollowedEntryDao
    abstract fun transactionRunnerDao(): TransactionRunnerDao
}