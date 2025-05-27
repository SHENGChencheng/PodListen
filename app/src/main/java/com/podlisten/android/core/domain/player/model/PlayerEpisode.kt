package com.podlisten.android.core.domain.player.model

import com.podlisten.android.core.data.database.model.EpisodeToPodcast
import com.podlisten.android.core.domain.model.EpisodeInfo
import com.podlisten.android.core.domain.model.PodcastInfo
import java.time.Duration
import java.time.OffsetDateTime

/**
 * Episode data with necessary information to be used within a player
 */
data class PlayerEpisode(
    val uri: String = "",
    val title: String = "",
    val subTitle: String = "",
    val published: OffsetDateTime = OffsetDateTime.MIN,
    val duration: Duration? = null,
    val podcastName: String = "",
    val author: String = "",
    val summary: String = "",
    val podcastImageUrl: String = ""
) {
    constructor(podcastInfo: PodcastInfo, episodeInfo: EpisodeInfo) : this(
        title = episodeInfo.title,
        subTitle = episodeInfo.subTitle,
        published = episodeInfo.published,
        duration = episodeInfo.duration,
        podcastName = podcastInfo.title,
        author = episodeInfo.author,
        summary = episodeInfo.summary,
        podcastImageUrl = podcastInfo.imageUrl,
        uri = episodeInfo.uri
    )
}

fun EpisodeToPodcast.toPlayerEpisode(): PlayerEpisode =
    PlayerEpisode(
        uri = episode.uri,
        title = episode.title,
        subTitle = episode.subtitle ?: "",
        published = episode.published,
        duration = episode.duration,
        podcastName = podcast.title,
        author = episode.author ?: "",
        summary = episode.summary ?: "",
        podcastImageUrl = podcast.imageUrl ?: ""
    )


