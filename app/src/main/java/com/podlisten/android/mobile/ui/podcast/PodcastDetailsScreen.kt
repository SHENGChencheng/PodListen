package com.podlisten.android.mobile.ui.podcast

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podlisten.android.core.domain.model.EpisodeInfo
import com.podlisten.android.mobile.ui.shared.Loading

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