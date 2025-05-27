package com.podlisten.android.core.domain.player

import com.podlisten.android.core.domain.player.model.PlayerEpisode
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration

val DefaultPlaybackSpeed = Duration.ofSeconds(1)

data class EpisodePlayerState(
    val currentEpisode: PlayerEpisode? = null,
    val queue: List<PlayerEpisode> = emptyList(),
    val playbackSpeed: Duration = DefaultPlaybackSpeed,
    val isPlaying: Boolean = false,
    val timeElapsed: Duration = Duration.ZERO
)

interface EpisodePlayer {
    val playerState: StateFlow<EpisodePlayerState>

    var currentEpisode: PlayerEpisode?

    var playerSpeed: Duration

    fun addToQueue(episode: PlayerEpisode)

    fun removeAllFromQueue()

    fun play()

    fun play(playerEpisode: PlayerEpisode)

    fun play(playerEpisodes: List<PlayerEpisode>)

    fun pause()

    fun stop()

    fun next()

    fun previous()

    fun advanceBy(duration: Duration)

    fun rewindBy(duration: Duration)

    fun onSeekingStarted()

    fun onSeekingFinished(duration: Duration)

    fun increaseSpeed(speed: Duration = Duration.ofMillis(500))

    fun decreaseSpeed(speed: Duration = Duration.ofMillis(500))
}