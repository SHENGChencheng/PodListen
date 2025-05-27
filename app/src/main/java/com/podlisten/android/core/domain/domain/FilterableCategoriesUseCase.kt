package com.podlisten.android.core.domain.domain

import com.podlisten.android.core.data.repository.CategoryStore
import com.podlisten.android.core.domain.model.CategoryInfo
import com.podlisten.android.core.domain.model.FilterableCategoriesModel
import com.podlisten.android.core.domain.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilterableCategoriesUseCase @Inject constructor(
    private val categoryStore: CategoryStore
) {
    operator fun invoke(selectedCategory: CategoryInfo?): Flow<FilterableCategoriesModel> =
        categoryStore.categoriesSortedByPodcastCount()
            .map { categories ->
                FilterableCategoriesModel(
                    categories = categories.map { it.asExternalModel() },
                    selectedCategory = selectedCategory
                        ?: categories.firstOrNull()?.asExternalModel()
                )
            }
}