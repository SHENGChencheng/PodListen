package com.podlisten.android.mobile.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.allVerticalHingeBounds
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.HingePolicy
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.occludingVerticalHingeBounds
import androidx.compose.material3.adaptive.separatingVerticalHingeBounds
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.podlisten.android.R
import com.podlisten.android.core.domain.model.EpisodeInfo
import com.podlisten.android.core.domain.model.FilterableCategoriesModel
import com.podlisten.android.core.domain.model.LibraryInfo
import com.podlisten.android.core.domain.model.PodcastCategoryFilterResult
import com.podlisten.android.core.domain.model.PodcastInfo
import com.podlisten.android.mobile.ui.component.PodcastImage
import com.podlisten.android.mobile.ui.component.ToggleFollowPodcastIconButton
import com.podlisten.android.mobile.ui.home.discover.discoverItems
import com.podlisten.android.mobile.ui.home.library.libraryItems
import com.podlisten.android.ui.theme.PodListenTheme
import com.podlisten.android.util.fullWidthItem
import com.podlisten.android.util.isCompact
import com.podlisten.android.util.quantityStringResource
import com.podlisten.android.util.radialGradientScrim
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isMainPaneHidden(): Boolean {
    return scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden
}

/**
 * Copied from `calculatePaneScaffoldDirective()` in [PaneScaffoldDirective], with modifications to
 * only show 1 pane horizontally if either width or height size class is compact.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun calculateScaffoldDirective(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    verticalHingePolicy: HingePolicy = HingePolicy.AvoidSeparating
): PaneScaffoldDirective {
    val maxHorizontalPartitions: Int
    val verticalSpacerSize: Dp
    if (windowAdaptiveInfo.windowSizeClass.isCompact) {
        // Window width or height is compact. Limit to 1 pane horizontally.
        maxHorizontalPartitions = 1
        verticalSpacerSize = 0.dp
    } else {
        when (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> {
                maxHorizontalPartitions = 1
                verticalSpacerSize = 0.dp
            }

            WindowWidthSizeClass.MEDIUM -> {
                maxHorizontalPartitions = 1
                verticalSpacerSize = 0.dp
            }

            else -> {
                maxHorizontalPartitions = 2
                verticalSpacerSize = 24.dp
            }
        }
    }
    val maxVerticalPartitions: Int
    val horizontalSpacerSize: Dp

    if (windowAdaptiveInfo.windowPosture.isTabletop) {
        maxVerticalPartitions = 2
        horizontalSpacerSize = 24.dp
    } else {
        maxVerticalPartitions = 1
        horizontalSpacerSize = 0.dp
    }

    val defaultPanePreferredWidth = 360.dp

    return PaneScaffoldDirective(
        maxHorizontalPartitions,
        verticalSpacerSize,
        maxVerticalPartitions,
        horizontalSpacerSize,
        defaultPanePreferredWidth,
        getExcludedVerticalBounds(windowAdaptiveInfo.windowPosture, verticalHingePolicy)
    )
}

/**
 * Copied from `getExcludedVerticalBounds()` in [PaneScaffoldDirective] since it is private.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun getExcludedVerticalBounds(posture: Posture, hingePolicy: HingePolicy): List<Rect> {
    return when (hingePolicy) {
        HingePolicy.AvoidSeparating -> posture.separatingVerticalHingeBounds
        HingePolicy.AvoidOccluding -> posture.occludingVerticalHingeBounds
        HingePolicy.AlwaysAvoid -> posture.allVerticalHingeBounds
        else -> emptyList()
    }
}

@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeScreenUiState by viewModel.state.collectAsStateWithLifecycle()
    val uiState = homeScreenUiState
    Box {
        HomeScreenReady(
            uiState = uiState,
            windowSizeClass = windowSizeClass,
            navigateToPlayer = navigateToPlayer,
            viewModel = viewModel
        )

        if (uiState.errorMessage != null) {
            HomeScreenError(onRetry = viewModel::refresh)
        }
    }
}

@Composable
private fun HomeScreenError(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.an_error_has_occurred)
            )
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.retry_label))
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenErrorPreview() {
    PodListenTheme {
        HomeScreenError(onRetry = {})
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun HomeScreenReady(
    uiState: HomeScreenUiState,
    windowSizeClass: WindowSizeClass,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val navigator = rememberSupportingPaneScaffoldNavigator<String>(
        scaffoldDirective = calculateScaffoldDirective(currentWindowAdaptiveInfo())
    )
    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = navigator.canNavigateBack()) {
        coroutineScope.launch {
            navigator.navigateBack()
        }
    }

    Surface {
        SupportingPaneScaffold(
            modifier = Modifier.fillMaxSize(),
            value = navigator.scaffoldValue,
            directive = navigator.scaffoldDirective,
            mainPane = {
                HomeScreen(
                    windowSizeClass = windowSizeClass,
                    isLoading = uiState.isLoading,
                    featuredPodcasts = uiState.featuredPodcasts,
                    selectedHomeCategory = uiState.selectedHomeCategory,
                    homeCategories = uiState.homeCategories,
                    filterableCategoriesModel = uiState.filterableCategoriesModel,
                    podcastCategoryFilterResult = uiState.podcastCategoryFilterResult,
                    library = uiState.library,
                    onHomeAction = viewModel::onHomeAction,
                    navigateToPodcastDetails = {
                        coroutineScope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting, it.uri)
                        }
                    },
                    navigateToPlayer = navigateToPlayer,
                )
            },
            supportingPane = {
                // TODO： PodcastDetailsScreen
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeAppBar(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
) {
    var queryText by remember{ mutableStateOf("") }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        SearchBar(
            modifier = if (isExpanded) Modifier.fillMaxWidth() else Modifier,
            query = queryText,
            onQueryChange = { queryText = it },
            onSearch = {},
            placeholder = {
                Text(text = stringResource(R.string.search_for_a_podcast))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = stringResource(R.string.cd_account)
                )
            },
            active = false,
            onActiveChange = {},
        ) {}
    }
}

@Preview
@Composable
private fun HomeAppBarPreview() {
    PodListenTheme {
        HomeAppBar(
            isExpanded = false,
        )
    }
}

@Composable
private fun HomeScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .radialGradientScrim(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        ) {
            content()
        }
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    isLoading: Boolean,
    featuredPodcasts: PersistentList<PodcastInfo>,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    library: LibraryInfo,
    onHomeAction: (HomeAction) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
) {
    LaunchedEffect(key1 = featuredPodcasts) {
        if (featuredPodcasts.isEmpty()) {
            onHomeAction(HomeAction.HomeCategorySelected(HomeCategory.Discover))
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    HomeScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Scaffold(
            topBar = {
                Column {
                    HomeAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        isExpanded = windowSizeClass.isCompact
                    )
                    if (isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = Color.Transparent
        ) { contentPadding ->
            // Main Content
            val snackBarText = stringResource(R.string.episode_added_to_your_queue)
            val showHomeCategoryTabs = featuredPodcasts.isNotEmpty() && homeCategories.isNotEmpty()
            HomeContent(
                modifier = Modifier.padding(contentPadding),
                showHomeCategoryTabs = showHomeCategoryTabs,
                featuredPodcasts = featuredPodcasts,
                selectedHomeCategory = selectedHomeCategory,
                homeCategories = homeCategories,
                filterableCategoriesModel = filterableCategoriesModel,
                podcastCategoryFilterResult = podcastCategoryFilterResult,
                library = library,
                onHomeAction = { action ->
                    if (action is HomeAction.QueueEpisode) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(snackBarText)
                        }
                    }
                    onHomeAction(action)
                },
                navigateToPodcastDetails = navigateToPodcastDetails,
                navigateToPlayer = navigateToPlayer
            )
        }
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    showHomeCategoryTabs: Boolean,
    featuredPodcasts: PersistentList<PodcastInfo>,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    library: LibraryInfo,
    onHomeAction: (HomeAction) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
) {
    val pagerState = rememberPagerState { featuredPodcasts.size }
    LaunchedEffect(pagerState, featuredPodcasts) {
        snapshotFlow { pagerState.currentPage }
            .collect {
                val podcast = featuredPodcasts.getOrNull(it)
                onHomeAction(HomeAction.LibraryPodcastSelected(podcast))
            }
    }

    HomeContentGrid(
        modifier = modifier,
        showHomeCategoryTabs = showHomeCategoryTabs,
        pagerState = pagerState,
        featuredPodcasts = featuredPodcasts,
        selectedHomeCategory = selectedHomeCategory,
        homeCategories = homeCategories,
        filterableCategoriesModel = filterableCategoriesModel,
        podcastCategoryFilterResult = podcastCategoryFilterResult,
        library = library,
        onHomeAction = onHomeAction,
        navigateToPodcastDetails = navigateToPodcastDetails,
        navigateToPlayer = navigateToPlayer
    )
}

@Composable
private fun HomeContentGrid(
    modifier: Modifier = Modifier,
    showHomeCategoryTabs: Boolean,
    pagerState: PagerState,
    featuredPodcasts: PersistentList<PodcastInfo>,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    library: LibraryInfo,
    onHomeAction: (HomeAction) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(362.dp)
    ) {
        if (featuredPodcasts.isNotEmpty()) {
            fullWidthItem {
                FollowedPodcastItem(
                    modifier = modifier.fillMaxWidth(),
                    pagerState = pagerState,
                    items = featuredPodcasts,
                    onPodcastUnfollowed = { onHomeAction(HomeAction.PodcastUnfollowed(it)) },
                    navigateToPodcastDetails = navigateToPodcastDetails
                )
            }
        }

        if (showHomeCategoryTabs) {
            fullWidthItem {
                Row {
                    HomeCategoryTabs(
                        modifier = Modifier.width(240.dp),
                        categories = homeCategories,
                        selectedCategory = selectedHomeCategory,
                        showHorizontalLine = false,
                        onCategorySelected = { onHomeAction(HomeAction.HomeCategorySelected(it)) },
                    )
                }
            }
        }

        when (selectedHomeCategory) {
            HomeCategory.Library -> {
                libraryItems()
            }

            HomeCategory.Discover -> {
                discoverItems(
                    filterableCategoriesModel = filterableCategoriesModel,
                    podcastCategoryFilterResult = podcastCategoryFilterResult,
                    navigateToPodcastDetails = navigateToPodcastDetails,
                    navigateToPlayer = navigateToPlayer,
                    onCategorySelected = { onHomeAction(HomeAction.CategorySelected(it)) },
                    onTogglePodcastFollowed = { onHomeAction(HomeAction.TogglePodcastFollowed(it)) },
                    onQueueEpisode = { onHomeAction(HomeAction.QueueEpisode(it)) }
                )
            }
        }
    }
}

@Composable
private fun FollowedPodcastItem(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    items: PersistentList<PodcastInfo>,
    onPodcastUnfollowed: (PodcastInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
) {
    Column(modifier = modifier) {
        Spacer(Modifier.height(16.dp))

        FollowedPodcasts(
            modifier = Modifier.fillMaxWidth(),
            pagerState = pagerState,
            items = items,
            onPodcastUnfollowed = onPodcastUnfollowed,
            navigateToPodcastDetails = navigateToPodcastDetails
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun HomeCategoryTabs(
    modifier: Modifier,
    categories: List<HomeCategory>,
    selectedCategory: HomeCategory,
    onCategorySelected: (HomeCategory) -> Unit,
    showHorizontalLine: Boolean,
) {
    if (categories.isEmpty()) {
        return
    }

    val selectedIndex = categories.indexOfFirst { it == selectedCategory }
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        HomeCategoryTabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
        )
    }

    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        indicator = indicator,
        divider = {
            if (showHorizontalLine) {
                HorizontalDivider()
            }
        }
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onCategorySelected(category) }
            ) {
                Text(
                    text = when (category) {
                        HomeCategory.Library -> stringResource(R.string.home_library)
                        HomeCategory.Discover -> stringResource(R.string.home_discover)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun HomeCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}

private val FEATURED_PODCAST_IMAGE_SIZE_DP = 160.dp

@Composable
private fun FollowedPodcasts(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    items: PersistentList<PodcastInfo>,
    onPodcastUnfollowed: (PodcastInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier.background(Color.Transparent)
    ) {
        val horizontalPadding = (this.maxWidth - FEATURED_PODCAST_IMAGE_SIZE_DP) / 2
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(
                horizontal = horizontalPadding,
                vertical = 16.dp
            ),
            pageSpacing = 24.dp,
            pageSize = PageSize.Fixed(FEATURED_PODCAST_IMAGE_SIZE_DP)
        ) { page ->
            val podcast = items[page]
            FollowedPodcastCarouselItem(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        navigateToPodcastDetails(podcast)
                    },
                podcastTitle = podcast.title,
                podcastImageUrl = podcast.imageUrl,
                lastEpisodeDateText = podcast.lastEpisodeDate?.let { lastUpdated(it) },
                onUnfollowedClick = { onPodcastUnfollowed(podcast) }
            )
        }
    }
}

@Composable
private fun FollowedPodcastCarouselItem(
    modifier: Modifier = Modifier,
    podcastTitle: String,
    podcastImageUrl: String,
    lastEpisodeDateText: String? = null,
    onUnfollowedClick: () -> Unit,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(FEATURED_PODCAST_IMAGE_SIZE_DP)
                .align(Alignment.CenterHorizontally)
        ) {
            PodcastImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
                podcastImageUrl = podcastImageUrl,
                contentDescription = podcastTitle,
            )

            ToggleFollowPodcastIconButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                isFollowed = true,
                onClick = onUnfollowedClick,
            )
        }

        if (lastEpisodeDateText != null) {
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally),
                text = lastEpisodeDateText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun lastUpdated(updated: OffsetDateTime): String {
    val duration = Duration.between(updated.toLocalDateTime(), LocalDateTime.now())
    val days = duration.toDays().toInt()

    return when {
        days > 28 -> stringResource(R.string.updated_longer)
        days > 7 -> {
            val weeks = days / 7
            quantityStringResource(R.plurals.updated_weeks_ago, weeks, weeks)
        }
        days > 0 -> quantityStringResource(R.plurals.updated_days_ago, days, days)
        else -> stringResource(R.string.updated_today)
    }
}

@Composable
@Preview
private fun PreviewPodcastCard() {
    PodListenTheme {
        FollowedPodcastCarouselItem(
            modifier = Modifier.size(128.dp),
            podcastTitle = "",
            podcastImageUrl = "",
            onUnfollowedClick = {}
        )
    }
}

