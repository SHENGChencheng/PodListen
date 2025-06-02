package com.podlisten.android.mobile.ui.home.discover

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.podlisten.android.R
import com.podlisten.android.core.domain.model.CategoryInfo
import com.podlisten.android.core.domain.model.EpisodeInfo
import com.podlisten.android.core.domain.model.FilterableCategoriesModel
import com.podlisten.android.core.domain.model.PodcastCategoryFilterResult
import com.podlisten.android.core.domain.model.PodcastInfo
import com.podlisten.android.core.domain.player.model.PlayerEpisode
import com.podlisten.android.mobile.ui.home.category.podcastCategory
import com.podlisten.android.ui.theme.Keyline1
import com.podlisten.android.util.fullWidthItem

fun LazyGridScope.discoverItems(
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
) {
    if (filterableCategoriesModel.isEmpty) {
        return
    }

    fullWidthItem {
        Spacer(Modifier.height(8.dp))

        PodcastCategoryTabs(
            modifier = Modifier.fillMaxWidth(),
            filterableCategoriesModel = filterableCategoriesModel,
            onCategorySelected = onCategorySelected,
        )

        Spacer(Modifier.height(8.dp))
    }

    podcastCategory(
        podcastCategoryFilterResult = podcastCategoryFilterResult,
        navigateToPodcastDetails = navigateToPodcastDetails,
        navigateToPlayer = navigateToPlayer,
        onQueueEpisode = onQueueEpisode,
        onTogglePodcastFollowed = onTogglePodcastFollowed
    )
}

@Composable
fun PodcastCategoryTabs(
    modifier: Modifier = Modifier,
    filterableCategoriesModel: FilterableCategoriesModel,
    onCategorySelected: (CategoryInfo) -> Unit,
) {
    val selectedIndex = filterableCategoriesModel.categories.indexOf(
        filterableCategoriesModel.selectedCategory
    )

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Keyline1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        itemsIndexed(
            items = filterableCategoriesModel.categories,
            key = { i, category -> category.id }
        ) { index, category ->
            ChoiceChipContent(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp),
                text = category.name,
                selected = index == selectedIndex,
                onClick = { onCategorySelected(category) },
            )
        }
    }
}

@Composable
private fun ChoiceChipContent(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        leadingIcon = {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.cd_selected_category),
                    modifier = Modifier.height(18.dp)
                )
            }
        },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        colors = FilterChipDefaults.filterChipColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        shape = MaterialTheme.shapes.medium,
        border = null,
    )
}