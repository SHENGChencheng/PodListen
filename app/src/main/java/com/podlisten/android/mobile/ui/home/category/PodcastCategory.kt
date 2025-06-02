package com.podlisten.android.mobile.ui.home.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.podlisten.android.core.domain.model.EpisodeInfo
import com.podlisten.android.core.domain.model.PodcastCategoryFilterResult
import com.podlisten.android.core.domain.model.PodcastInfo
import com.podlisten.android.core.domain.player.model.PlayerEpisode
import com.podlisten.android.mobile.ui.component.PodcastImage
import com.podlisten.android.mobile.ui.component.ToggleFollowPodcastIconButton
import com.podlisten.android.mobile.ui.shared.EpisodeListItem
import com.podlisten.android.ui.theme.Keyline1
import com.podlisten.android.util.fullWidthItem

fun LazyGridScope.podcastCategory(
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
) {
    fullWidthItem {
        CategoryPodcasts(
            podcastCategoryFilterResult.topPodcasts,
            navigateToPodcastDetails,
            onTogglePodcastFollowed
        )
    }

    val episodes = podcastCategoryFilterResult.episodes
    items(episodes, key = { it.episode.uri} ) { item ->
        EpisodeListItem(
            modifier = Modifier.fillMaxWidth(),
            episode = item.episode,
            podcast = item.podcast,
            onClick = navigateToPlayer,
            onQueueEpisode = onQueueEpisode,
        )
    }
}

@Composable
private fun CategoryPodcasts(
    topPodcasts: List<PodcastInfo>,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit
) {
    CategoryPodcastRow(
        modifier = Modifier.fillMaxWidth(),
        podcasts = topPodcasts,
        navigateToPodcastDetails = navigateToPodcastDetails,
        onTogglePodcastFollowed = onTogglePodcastFollowed
    )
}

@Composable
private fun CategoryPodcastRow(
    modifier: Modifier = Modifier,
    podcasts: List<PodcastInfo>,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = Keyline1,
            top = 8.dp,
            end = Keyline1,
            bottom = 24.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(
            items = podcasts,
            key = { it.uri }
        ) { podcast ->
            TopPodcastRowItem(
                modifier = Modifier
                    .width(128.dp)
                    .clickable { navigateToPodcastDetails(podcast) },
                podcastTitle = podcast.title,
                podcastImageUrl = podcast.imageUrl,
                isFollowed = podcast.isSubscribed ?: false,
                onToggleFollowClicked = { onTogglePodcastFollowed(podcast) }
            )
        }
    }
}

@Composable
private fun TopPodcastRowItem(
    modifier: Modifier = Modifier,
    podcastTitle: String,
    podcastImageUrl: String,
    isFollowed: Boolean,
    onToggleFollowClicked: () -> Unit
) {
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            PodcastImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
                podcastImageUrl = podcastImageUrl,
                contentDescription = podcastTitle
            )

            ToggleFollowPodcastIconButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                isFollowed = isFollowed,
                onClick = onToggleFollowClicked
            )
        }

        Text(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            text = podcastTitle,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}