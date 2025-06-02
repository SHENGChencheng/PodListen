package com.podlisten.android.mobile.ui.home.library

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.podlisten.android.R
import com.podlisten.android.core.domain.model.EpisodeInfo
import com.podlisten.android.core.domain.model.LibraryInfo
import com.podlisten.android.core.domain.player.model.PlayerEpisode
import com.podlisten.android.mobile.ui.shared.EpisodeListItem
import com.podlisten.android.ui.theme.Keyline1
import com.podlisten.android.util.fullWidthItem

fun LazyGridScope.libraryItems(
    library: LibraryInfo,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
) {
    fullWidthItem {
        Text(
            modifier = Modifier.padding(
                start = Keyline1,
                top = 16.dp
            ),
            text = stringResource(R.string.latest_episodes),
            style = MaterialTheme.typography.headlineLarge
        )
    }

    items(library, key = { it.episode.uri }) { item ->
        EpisodeListItem(
            modifier = Modifier.fillMaxWidth(),
            episode = item.episode,
            podcast = item.podcast,
            onClick = navigateToPlayer,
            onQueueEpisode = onQueueEpisode,
        )
    }
}