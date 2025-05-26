package com.podlisten.android.core.data.repository

import com.podlisten.android.core.data.database.dao.CategoriesDao
import com.podlisten.android.core.data.database.dao.EpisodesDao
import com.podlisten.android.core.data.database.dao.PodcastCategoryEntryDao
import com.podlisten.android.core.data.database.dao.PodcastsDao
import com.podlisten.android.core.data.database.model.Category
import com.podlisten.android.core.data.database.model.EpisodeToPodcast
import com.podlisten.android.core.data.database.model.PodcastCategoryEntry
import com.podlisten.android.core.data.database.model.PodcastWithExtraInfo
import kotlinx.coroutines.flow.Flow

interface CategoryStore {

    fun categoriesSortedByPodcastCount(
        limit: Int = Int.MAX_VALUE
    ): Flow<List<Category>>

    fun podcastsInCategorySortedByPodcastCount(
        categoryId: Long,
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>>

    fun episodesFromPodcastsInCategory(
        categoryId: Long,
        limit: Int = Int.MAX_VALUE
    ): Flow<List<EpisodeToPodcast>>

    suspend fun  addCategory(category: Category): Long

    suspend fun addPodcastToCategory(podcastUri: String, categoryId: Long)

    fun getCategory(name: String): Flow<Category?>
}

class LocalCategoryStore constructor(
    private val categoriesDao: CategoriesDao,
    private val categoryEntryDao: PodcastCategoryEntryDao,
    private val episodesDao: EpisodesDao,
    private val podcastsDao: PodcastsDao
) : CategoryStore {
    override fun categoriesSortedByPodcastCount(limit: Int): Flow<List<Category>> {
        return categoriesDao.categoriesSortedByPodcastCount(limit)
    }

    override fun podcastsInCategorySortedByPodcastCount(
        categoryId: Long,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastsDao.podcastsInCategorySortedByLastEpisode(categoryId, limit)
    }

    override fun episodesFromPodcastsInCategory(
        categoryId: Long,
        limit: Int
    ): Flow<List<EpisodeToPodcast>> {
        return episodesDao.episodesFromPodcastInCategory(categoryId, limit)
    }

    override suspend fun addCategory(category: Category): Long {
        return when (val local = categoriesDao.getCategoryWithName(category.name)) {
            null -> categoriesDao.insert(category)
            else -> local.id
        }
    }

    override suspend fun addPodcastToCategory(podcastUri: String, categoryId: Long) {
        categoryEntryDao.insert(
            PodcastCategoryEntry(podcastUri = podcastUri, categoryId = categoryId)
        )
    }

    override fun getCategory(name: String): Flow<Category?> =
        categoriesDao.observeCategory(name)
}