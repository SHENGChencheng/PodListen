package com.podlisten.android.mobile.ui.podcast

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podlisten.android.core.data.repository.EpisodeStore
import com.podlisten.android.core.data.repository.PodcastStore
import com.podlisten.android.core.domain.model.EpisodeInfo
import com.podlisten.android.core.domain.model.PodcastInfo
import com.podlisten.android.core.domain.model.asExternalModel
import com.podlisten.android.core.domain.player.EpisodePlayer
import com.podlisten.android.core.domain.player.model.PlayerEpisode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PodcastUiState {
    data object Loading : PodcastUiState
    data class Ready(
        val podcast: PodcastInfo,
        val episodes: List<EpisodeInfo>,
    ) : PodcastUiState
}

@HiltViewModel(assistedFactory = PodcastDetailsViewModel.Factory::class)
class PodcastDetailsViewModel @AssistedInject constructor(
    private val episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
    private val podcastStore: PodcastStore,
    @Assisted private val podcastUri: String,
) : ViewModel() {

    private val decodedPodcastUri = Uri.decode(podcastUri)

    val state: StateFlow<PodcastUiState> =
        combine(
            podcastStore.podcastWithExtraInfo(decodedPodcastUri),
            episodeStore.episodesInPodcast(decodedPodcastUri),
        ) { podcast, episodeToPodcasts ->
            val episodes = episodeToPodcasts.map { it.episode.asExternalModel() }
            PodcastUiState.Ready(
                podcast = podcast.podcast.asExternalModel().copy(isSubscribed = podcast.isFollowed),
                episodes = episodes,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_1000),
            initialValue = PodcastUiState.Loading
        )

    fun toggleSubscribe(podcast: PodcastInfo) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcast.uri)
        }
    }

    fun onQueueEpisode(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(playerEpisode)
    }

    @AssistedFactory
    interface Factory {
        fun create(podcastUri: String): PodcastDetailsViewModel
    }
}