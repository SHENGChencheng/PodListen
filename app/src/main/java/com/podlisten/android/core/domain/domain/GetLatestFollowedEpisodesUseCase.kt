package com.podlisten.android.core.domain.domain

import com.podlisten.android.core.data.database.model.EpisodeToPodcast
import com.podlisten.android.core.data.repository.EpisodeStore
import com.podlisten.android.core.data.repository.PodcastStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetLatestFollowedEpisodesUseCase @Inject constructor(
    private val episodeStore: EpisodeStore,
    private val podcastStore: PodcastStore,
) {
    operator fun invoke(): Flow<List<EpisodeToPodcast>> =
        podcastStore.followedPodcastsSortedByLastEpisode()
            .flatMapLatest { followedPodcasts ->
                episodeStore.episodesInPodcasts(
                    followedPodcasts.map { it.podcast.uri },
                    followedPodcasts.size * 5
                )
            }
}