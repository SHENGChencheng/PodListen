package com.podlisten.android.core.data.repository

import com.podlisten.android.core.data.database.dao.PodcastFollowedEntryDao
import com.podlisten.android.core.data.database.dao.PodcastsDao
import com.podlisten.android.core.data.database.dao.TransactionRunner
import com.podlisten.android.core.data.database.model.Category
import com.podlisten.android.core.data.database.model.Podcast
import com.podlisten.android.core.data.database.model.PodcastFollowedEntry
import com.podlisten.android.core.data.database.model.PodcastWithExtraInfo
import kotlinx.coroutines.flow.Flow

interface PodcastStore {
    fun podcastWithUri(podcastUri: String): Flow<Podcast>

    fun podcastWithExtraInfo(podcastUri: String): Flow<PodcastWithExtraInfo>

    fun podcastsSortedByLastEpisode(
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>>

    fun followedPodcastsSortedByLastEpisode(
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>>

    fun searchPodcastByTitle(
        keyword: String,
        limit: Int = Int.MAX_VALUE,
    ): Flow<List<PodcastWithExtraInfo>>

    fun searchPodcastByTitleAndCategories(
        keyword: String,
        categories: List<Category>,
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>>

    suspend fun togglePodcastFollowed(podcastUri: String)

    suspend fun followPodcast(podcastUri: String)

    suspend fun unfollowPodcast(podcastUri: String)

    suspend fun addPodcast(podcast: Podcast)

    suspend fun isEmpty(): Boolean
}

class LocalPodcastStore constructor(
    private val podcastDao: PodcastsDao,
    private val podcastFollowedEntryDao: PodcastFollowedEntryDao,
    private val transactionRunner: TransactionRunner
) : PodcastStore {
    override fun podcastWithUri(podcastUri: String): Flow<Podcast> {
        return podcastDao.podcastWithUri(podcastUri)
    }

    override fun podcastWithExtraInfo(podcastUri: String): Flow<PodcastWithExtraInfo> {
        return podcastDao.podcastWithExtraInfo(podcastUri)
    }

    override fun podcastsSortedByLastEpisode(limit: Int): Flow<List<PodcastWithExtraInfo>> {
        return podcastDao.podcastsSortedByLastEpisode(limit)
    }

    override fun followedPodcastsSortedByLastEpisode(limit: Int): Flow<List<PodcastWithExtraInfo>> {
        return podcastDao.followedPodcastsSortedByLastEpisode(limit)
    }

    override fun searchPodcastByTitle(
        keyword: String,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastDao.searchPodcastByTitle(keyword, limit)
    }

    override fun searchPodcastByTitleAndCategories(
        keyword: String,
        categories: List<Category>,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        val categoryIdList = categories.map { it.id }
        return podcastDao.searchPodcastByTitleAndCategory(keyword, categoryIdList, limit)
    }

    override suspend fun followPodcast(podcastUri: String) {
        podcastFollowedEntryDao.insert(PodcastFollowedEntry(podcastUri = podcastUri))
    }

    override suspend fun togglePodcastFollowed(podcastUri: String) = transactionRunner {
        if (podcastFollowedEntryDao.isPodcastFollowed(podcastUri)) {
            unfollowPodcast(podcastUri)
        } else {
            followPodcast(podcastUri)
        }
    }

    override suspend fun unfollowPodcast(podcastUri: String) {
        podcastFollowedEntryDao.deleteWithPodcastUri(podcastUri)
    }

    override suspend fun addPodcast(podcast: Podcast) {
        podcastDao.insert(podcast)
    }

    override suspend fun isEmpty(): Boolean = podcastDao.count() == 0
}
