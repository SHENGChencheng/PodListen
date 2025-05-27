package com.podlisten.android.core.domain.model

import com.podlisten.android.core.data.database.model.EpisodeToPodcast

data class PodcastToEpisodeInfo(
    val episode: EpisodeInfo,
    val podcast: PodcastInfo,
)

fun EpisodeToPodcast.asPodcastToEpisodeInfo(): PodcastToEpisodeInfo =
    PodcastToEpisodeInfo(
        episode = episode.asExternalModel(),
        podcast = podcast.asExternalModel()
    )
