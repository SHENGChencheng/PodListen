package com.podlisten.android.mobile.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.podlisten.android.R
import com.podlisten.android.core.domain.model.EpisodeInfo
import com.podlisten.android.core.domain.model.PodcastInfo
import com.podlisten.android.core.domain.player.model.PlayerEpisode
import com.podlisten.android.mobile.ui.component.PodcastImage
import com.podlisten.android.mobile.ui.testing.PreviewEpisodes
import com.podlisten.android.mobile.ui.testing.PreviewPodcasts
import com.podlisten.android.ui.theme.PodListenTheme
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun EpisodeListItem(
    modifier: Modifier = Modifier,
    episode: EpisodeInfo,
    podcast: PodcastInfo,
    onClick: (EpisodeInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    showPodcastImage: Boolean = true,
    showSummary: Boolean = false,
) {
    Box(modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { onClick(episode) }
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                EpisodeListItemHeader(
                    modifier = Modifier.padding(bottom = 8.dp),
                    episode = episode,
                    podcast = podcast,
                    showPodcastImage = showPodcastImage,
                    showSummary = showSummary
                )
                EpisodeListItemFooter(
                    episode = episode,
                    podcast = podcast,
                    onQueueEpisode = onQueueEpisode
                )
            }
        }
    }
}

@Composable
private fun EpisodeListItemHeader(
    modifier: Modifier = Modifier,
    episode: EpisodeInfo,
    podcast: PodcastInfo,
    showPodcastImage: Boolean,
    showSummary: Boolean
) {
    Row(modifier = modifier) {
        Column(modifier = Modifier
            .weight(1f)
            .padding(end = 16.dp)
        ) {
            Text(
                modifier = Modifier.padding(vertical = 2.dp),
                text = episode.title,
                maxLines = 2,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )

            if (showSummary) {
                Text(
                    text = episode.summary,
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                )
            } else {
                Text(
                    text = podcast.title,
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
        if (showPodcastImage) {
            EpisodeListItemImage(
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium),
                podcast = podcast,
            )
        }
    }
}

@Composable
private fun EpisodeListItemImage(
    modifier: Modifier = Modifier,
    podcast: PodcastInfo
) {
    PodcastImage(
        modifier = modifier,
        podcastImageUrl = podcast.imageUrl,
        contentDescription = null,
    )
}

@Composable
private fun EpisodeListItemFooter(
    modifier: Modifier = Modifier,
    episode: EpisodeInfo,
    podcast: PodcastInfo,
    onQueueEpisode: (PlayerEpisode) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 24.dp)
                ) {  }
                .size(48.dp)
                .padding(6.dp)
                .semantics { role = Role.Button },
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = stringResource(R.string.cd_play),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        val duration = episode.duration
        Text(
            text = when {
                duration != null -> {
                    stringResource(
                        R.string.episode_date_duration,
                        MediumDateFormatter.format(episode.published),
                        duration.toMinutes().toInt()
                    )
                }
                else -> MediumDateFormatter.format(episode.published)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        )
        IconButton(
            onClick = {
                onQueueEpisode(
                    PlayerEpisode(
                        episodeInfo = episode,
                        podcastInfo = podcast
                    )
                )
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.cd_add),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.cd_more),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
private fun EpisodeListItemPreview() {
    PodListenTheme {
        EpisodeListItem(
            episode = PreviewEpisodes[0],
            podcast = PreviewPodcasts[0],
            onClick = {},
            onQueueEpisode = {},
            showSummary = true
        )
    }
}

private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}