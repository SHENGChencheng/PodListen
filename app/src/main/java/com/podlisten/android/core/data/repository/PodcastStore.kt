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
    private val podcastsDao: PodcastsDao,
    private val podcastFollowedEntryDao: PodcastFollowedEntryDao,
    private val transactionRunner: TransactionRunner
) : PodcastStore {
    override fun podcastWithUri(podcastUri: String): Flow<Podcast> {
        return podcastsDao.podcastWithUri(podcastUri)
    }

    override fun podcastWithExtraInfo(podcastUri: String): Flow<PodcastWithExtraInfo> {
        return podcastsDao.podcastWithExtraInfo(podcastUri)
    }

    override fun podcastsSortedByLastEpisode(limit: Int): Flow<List<PodcastWithExtraInfo>> {
        return podcastsDao.podcastsSortedByLastEpisode(limit)
    }

    override fun followedPodcastsSortedByLastEpisode(limit: Int): Flow<List<PodcastWithExtraInfo>> {
        return podcastsDao.followedPodcastsSortedByLastEpisode(limit)
    }

    override fun searchPodcastByTitle(
        keyword: String,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastsDao.searchPodcastByTitle(keyword, limit)
    }

    override fun searchPodcastByTitleAndCategories(
        keyword: String,
        categories: List<Category>,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        val categoryIdList = categories.map { it.id }
        return podcastsDao.searchPodcastByTitleAndCategory(keyword, categoryIdList, limit)
    }

    override suspend fun followPodcast(podcastUri: String) {
        podcastFollowedEntryDao.insert(PodcastFollowedEntry(podcastUri = podcastUri))
    }

    override suspend fun unfollowPodcast(podcastUri: String) {
        podcastFollowedEntryDao.deleteWithPodcastUri(podcastUri)
    }

    override suspend fun addPodcast(podcast: Podcast) {
        podcastsDao.insert(podcast)
    }

    override suspend fun isEmpty(): Boolean = podcastsDao.count() == 0
}
