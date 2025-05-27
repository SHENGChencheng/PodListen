package com.podlisten.android.core.domain.model

data class LibraryInfo(
    val episodes: List<PodcastToEpisodeInfo> = emptyList()
) : List<PodcastToEpisodeInfo> by episodes
