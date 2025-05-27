package com.podlisten.android.core.domain.domain

import com.podlisten.android.core.data.repository.CategoryStore
import com.podlisten.android.core.domain.model.CategoryInfo
import com.podlisten.android.core.domain.model.PodcastCategoryFilterResult
import com.podlisten.android.core.domain.model.asExternalModel
import com.podlisten.android.core.domain.model.asPodcastToEpisodeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class PodcastCategoryFilterUseCase @Inject constructor(
    private val categoryStore: CategoryStore
) {
    operator fun invoke(category: CategoryInfo?): Flow<PodcastCategoryFilterResult> {
        if (category == null) {
            return flowOf(PodcastCategoryFilterResult())
        }

        val recentPodcastsFlow = categoryStore.podcastsInCategorySortedByPodcastCount(
            categoryId = category.id,
            limit = 10
        )

        val episodesFlow = categoryStore.episodesFromPodcastsInCategory(
            categoryId = category.id,
            limit = 20
        )

        return combine(recentPodcastsFlow, episodesFlow) { topPodcasts, episodes ->
            PodcastCategoryFilterResult(
                topPodcasts = topPodcasts.map { it.asExternalModel() },
                episodes = episodes.map { it.asPodcastToEpisodeInfo() }
            )
        }
    }
}