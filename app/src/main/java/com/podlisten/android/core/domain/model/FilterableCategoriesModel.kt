package com.podlisten.android.core.domain.model

data class FilterableCategoriesModel(
    val categories: List<CategoryInfo> = emptyList(),
    val selectedCategory: CategoryInfo? = null
) {
    val isEmpty = categories.isEmpty() || selectedCategory == null
}
