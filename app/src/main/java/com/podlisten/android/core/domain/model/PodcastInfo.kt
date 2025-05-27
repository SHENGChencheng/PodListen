package com.podlisten.android.core.domain.model

import com.podlisten.android.core.data.database.model.Podcast
import com.podlisten.android.core.data.database.model.PodcastWithExtraInfo
import java.time.OffsetDateTime

data class PodcastInfo(
    val uri: String = "",
    val title: String = "",
    val author: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val isSubscribed: Boolean? = null,
    val lastEpisodeDate: OffsetDateTime? = null,
)

fun Podcast.asExternalModel(): PodcastInfo =
    PodcastInfo(
        uri = uri,
        title = title,
        author = author ?: "",
        imageUrl = imageUrl ?: "",
        description = description ?: "",
    )

fun PodcastWithExtraInfo.asExternalModel(): PodcastInfo =
    this.podcast.asExternalModel().copy(
        isSubscribed = isFollowed,
        lastEpisodeDate = lastEpisodeDate
    )