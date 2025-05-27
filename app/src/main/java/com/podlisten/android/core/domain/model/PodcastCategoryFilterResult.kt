package com.podlisten.android.core.domain.model

data class PodcastCategoryFilterResult(
    val topPodcasts: List<PodcastInfo> = emptyList(),
    val episodes: List<PodcastToEpisodeInfo> = emptyList()
)
