package com.podlisten.android.core.data.repository

import com.podlisten.android.core.data.database.dao.EpisodesDao
import com.podlisten.android.core.data.database.model.Episode
import com.podlisten.android.core.data.database.model.EpisodeToPodcast
import kotlinx.coroutines.flow.Flow

interface EpisodeStore {
    fun episodeWithUri(episodeUri: String): Flow<Episode>

    fun episodeAndPodcastWithUri(episodeUri: String): Flow<EpisodeToPodcast>

    fun episodesInPodcast(
        podcastUri: String,
        limit: Int = Int.MAX_VALUE
    ): Flow<List<EpisodeToPodcast>>

    fun episodesInPodcasts(
        podcastUris: List<String>,
        limit: Int = Int.MAX_VALUE
    ): Flow<List<EpisodeToPodcast>>

    suspend fun addEpisodes(episodes: Collection<Episode>)

    suspend fun isEmpty(): Boolean
}

class LocalEpisodeStore(
    private val episodesDao: EpisodesDao
) : EpisodeStore {
    override fun episodeWithUri(episodeUri: String): Flow<Episode> {
        return episodesDao.episode(episodeUri)
    }

    override fun episodeAndPodcastWithUri(episodeUri: String): Flow<EpisodeToPodcast> =
        episodesDao.episodeAndPodcast(episodeUri)

    override fun episodesInPodcast(podcastUri: String, limit: Int): Flow<List<EpisodeToPodcast>> {
        return episodesDao.episodesForPodcastUri(podcastUri, limit)
    }

    override fun episodesInPodcasts(
        podcastUris: List<String>,
        limit: Int
    ): Flow<List<EpisodeToPodcast>> {
        return episodesDao.episodesForPodcasts(podcastUris, limit)
    }

    override suspend fun addEpisodes(episodes: Collection<Episode>) =
        episodesDao.insertAll(episodes)

    override suspend fun isEmpty(): Boolean = episodesDao.count() == 0
}