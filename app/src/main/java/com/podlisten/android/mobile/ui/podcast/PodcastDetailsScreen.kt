package com.podlisten.android.mobile.ui.podcast

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podlisten.android.R
import com.podlisten.android.core.domain.model.EpisodeInfo
import com.podlisten.android.core.domain.model.PodcastInfo
import com.podlisten.android.core.domain.player.model.PlayerEpisode
import com.podlisten.android.mobile.ui.component.PodcastImage
import com.podlisten.android.mobile.ui.shared.Loading
import com.podlisten.android.ui.theme.Keyline1

@Composable
fun PodcastDetailsScreen(
    viewModel: PodcastDetailsViewModel,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    when (val s = state.value) {
        is PodcastUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is PodcastUiState.Ready -> {}
    }
}

@Composable
private fun PodcastDetailsLoadingScreen(
    modifier: Modifier = Modifier,
) {
    Loading(modifier = modifier)
}

@Composable
fun PodcastDetailsScreen(
    podcast: PodcastInfo,
    episodes: List<EpisodeInfo>,
    toggleSubscribe: (PodcastInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier,
) {

}

@Composable
fun PodcastDetailsHeaderItem(
    podcast: PodcastInfo,
    toggleSubscribe: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.padding(Keyline1)
    ) {
        val maxImageSize = this.maxWidth / 2
        val imageSize = minOf(maxImageSize, 148.dp)
        Column {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                PodcastImage(
                    contentDescription = podcast.title,
                    podcastImageUrl = podcast.imageUrl,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(MaterialTheme.shapes.large)
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = podcast.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    PodcastDetailsHeaderItemButton(
                        isSubscribed = podcast.isSubscribed ?: false,
                        onClick = { toggleSubscribe(podcast) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            PodcastDetailsDescription(
                podcast = podcast,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
fun PodcastDetailsDescription(
    podcast: PodcastInfo,
    modifier: Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showSeeMore by remember { mutableStateOf(false) }
    Box(modifier = modifier.clickable { isExpanded = !isExpanded }) {
        Text(
            text = podcast.description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result ->
                showSeeMore = result.hasVisualOverflow
            },
            modifier = Modifier.animateContentSize(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = EaseOutExpo
                )
            )
        )
        if (showSeeMore) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = stringResource(R.string.see_more),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun PodcastDetailsHeaderItemButton(
    isSubscribed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.padding(top = 16.dp)) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSubscribed)
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.secondary
            ),
        ) {
            Icon(
                imageVector = if (isSubscribed)
                    Icons.Default.Check
                else
                    Icons.Default.Add,
                contentDescription = null
            )
            Text(
                text = if (isSubscribed)
                    stringResource(R.string.subscribed)
                else
                    stringResource(R.string.subscribe),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.cd_more)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastDetailsTopAppBar(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back)
                )
            }
        },
        modifier = modifier
    )
}