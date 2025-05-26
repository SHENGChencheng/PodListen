package com.podlisten.android.core.data.repository

import com.podlisten.android.core.data.Dispatcher
import com.podlisten.android.core.data.PodListenDispatcher
import com.podlisten.android.core.data.database.dao.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PodcastsRepository @Inject constructor(
    // TODO: private val podcastsFetcher: PodcastsFetcher,
    private val podcastStore: PodcastStore,
    private val episodeStore: EpisodeStore,
    private val categoryStore: CategoryStore,
    private val transactionRunner: TransactionRunner,
    @Dispatcher(PodListenDispatcher.Main) mainDispatcher: CoroutineDispatcher,
) {
    private var refreshingJob: Job? = null

    private val scope = CoroutineScope(mainDispatcher)

    suspend fun updatePodcasts(force: Boolean) {
        if (refreshingJob?.isActive == true) {
            refreshingJob?.join()
        } else if (force || podcastStore.isEmpty()) {
            val job = scope.launch {
                // TODO: podcastsFetcher.fetchPodcasts()
            }
            refreshingJob = job
            job.join()
        }
    }
}