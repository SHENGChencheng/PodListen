package com.podlisten.android.core.domain.model

import com.podlisten.android.core.data.database.model.Episode
import java.time.Duration
import java.time.OffsetDateTime

data class EpisodeInfo(
    val uri: String = "",
    val title: String = "",
    val subTitle: String = "",
    val summary: String = "",
    val author: String = "",
    val published: OffsetDateTime = OffsetDateTime.MIN,
    val duration: Duration? = null,
)

fun Episode.asExternalModel(): EpisodeInfo =
    EpisodeInfo(
        uri = uri,
        title = title,
        subTitle = subtitle ?: "",
        summary = summary ?: "",
        author = author ?: "",
        published = published,
        duration = duration
    )
