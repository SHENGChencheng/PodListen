package com.podlisten.android.mobile.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podlisten.android.core.data.database.model.EpisodeToPodcast
import com.podlisten.android.core.data.repository.EpisodeStore
import com.podlisten.android.core.data.repository.PodcastStore
import com.podlisten.android.core.data.repository.PodcastsRepository
import com.podlisten.android.core.data.util.combine
import com.podlisten.android.core.domain.domain.FilterableCategoriesUseCase
import com.podlisten.android.core.domain.domain.PodcastCategoryFilterUseCase
import com.podlisten.android.core.domain.model.CategoryInfo
import com.podlisten.android.core.domain.model.FilterableCategoriesModel
import com.podlisten.android.core.domain.model.LibraryInfo
import com.podlisten.android.core.domain.model.PodcastCategoryFilterResult
import com.podlisten.android.core.domain.model.PodcastInfo
import com.podlisten.android.core.domain.model.asExternalModel
import com.podlisten.android.core.domain.model.asPodcastToEpisodeInfo
import com.podlisten.android.core.domain.player.EpisodePlayer
import com.podlisten.android.core.domain.player.model.PlayerEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    private val podcastStore: PodcastStore,
    private val episodeStore: EpisodeStore,
    private val podcastCategoryFilterUseCase: PodcastCategoryFilterUseCase,
    private val filterableCategoriesUseCase: FilterableCategoriesUseCase,
    private val episodePlayer: EpisodePlayer
) : ViewModel() {

    private val selectedLibraryPodcast = MutableStateFlow<PodcastInfo?>(null)
    private val selectedHomeCategory = MutableStateFlow(HomeCategory.Discover)
    private val homeCategories = MutableStateFlow(HomeCategory.entries)
    private val _selectedCategory = MutableStateFlow<CategoryInfo?>(null)
    private val _state = MutableStateFlow(HomeScreenUiState())
    private val refreshing = MutableStateFlow(false)
    private val subscribedPodcasts = podcastStore.followedPodcastsSortedByLastEpisode(limit = 10)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    val state: StateFlow<HomeScreenUiState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(
                homeCategories,
                selectedHomeCategory,
                subscribedPodcasts,
                refreshing,
                _selectedCategory.flatMapLatest { selectedCategory ->
                    filterableCategoriesUseCase(selectedCategory)
                },
                _selectedCategory.flatMapLatest { selectedCategory ->
                    podcastCategoryFilterUseCase(selectedCategory)
                },
                subscribedPodcasts.flatMapLatest { podcasts ->
                    episodeStore.episodesInPodcasts(
                        podcastUris = podcasts.map { it.podcast.uri },
                        limit = 20
                    )
                }
            ) { homeCategories,
                homeCategory,
                podcasts,
                refreshing,
                filterableCategories,
                podcastCategoryFilterResult,
                libraryEpisodes ->

                _selectedCategory.value = filterableCategories.selectedCategory
                selectedHomeCategory.value =
                    if (podcasts.isEmpty()) HomeCategory.Discover else homeCategory

                HomeScreenUiState(
                    isLoading = refreshing,
                    featuredPodcasts = podcasts.map { it.asExternalModel() }.toPersistentList(),
                    homeCategories = homeCategories,
                    selectedHomeCategory = homeCategory,
                    filterableCategoriesModel = filterableCategories,
                    podcastCategoryFilterResult = podcastCategoryFilterResult,
                    library = libraryEpisodes.asLibrary()
                )
            }.catch { throwable ->
                emit(
                    HomeScreenUiState(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                )
            }.collect {
                _state.value = it
            }
        }

        refresh(force = false)
    }

    fun refresh(force: Boolean = true) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                podcastsRepository.updatePodcasts(force)
            }

            refreshing.value = false
        }
    }

    fun onHomeAction(action: HomeAction) {
        when (action) {
            is HomeAction.CategorySelected -> onCategorySelected(action.category)
            is HomeAction.HomeCategorySelected -> onHomeCategorySelected(action.category)
            is HomeAction.LibraryPodcastSelected -> onLibraryPodcastSelected(action.podcast)
            is HomeAction.PodcastUnfollowed -> onPodcastUnfollowed(action.podcast)
            is HomeAction.QueueEpisode -> onQueueEpisode(action.episode)
            is HomeAction.TogglePodcastFollowed -> onTogglePodcastFollowed(action.podcast)
        }
    }

    private fun onCategorySelected(category: CategoryInfo) {
        _selectedCategory.value = category
    }

    private fun onHomeCategorySelected(category: HomeCategory) {
        selectedHomeCategory.value = category
    }

    private fun onPodcastUnfollowed(podcast: PodcastInfo) {
        viewModelScope.launch {
            podcastStore.unfollowPodcast(podcast.uri)
        }
    }

    private fun onTogglePodcastFollowed(podcast: PodcastInfo) {
        viewModelScope.launch {
            podcastStore.followPodcast(podcast.uri)
        }
    }

    private fun onLibraryPodcastSelected(podcast: PodcastInfo?) {
        selectedLibraryPodcast.value = podcast
    }

    private fun onQueueEpisode(episode: PlayerEpisode) {
        episodePlayer.addToQueue(episode)
    }
}

private fun List<EpisodeToPodcast>.asLibrary(): LibraryInfo =
    LibraryInfo(
        episodes = this.map { it.asPodcastToEpisodeInfo() }
    )

enum class HomeCategory {
    Library, Discover
}

@Immutable
sealed interface HomeAction {
    data class CategorySelected(val category: CategoryInfo) : HomeAction
    data class HomeCategorySelected(val category: HomeCategory) : HomeAction
    data class PodcastUnfollowed(val podcast: PodcastInfo) : HomeAction
    data class TogglePodcastFollowed(val podcast: PodcastInfo) : HomeAction
    data class LibraryPodcastSelected(val podcast: PodcastInfo?) : HomeAction
    data class QueueEpisode(val episode: PlayerEpisode) : HomeAction
}

@Immutable
data class HomeScreenUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val featuredPodcasts: PersistentList<PodcastInfo> = persistentListOf(),
    val selectedHomeCategory: HomeCategory = HomeCategory.Discover,
    val homeCategories: List<HomeCategory> = emptyList(),
    val filterableCategoriesModel: FilterableCategoriesModel = FilterableCategoriesModel(),
    val podcastCategoryFilterResult: PodcastCategoryFilterResult = PodcastCategoryFilterResult(),
    val library: LibraryInfo = LibraryInfo()
)