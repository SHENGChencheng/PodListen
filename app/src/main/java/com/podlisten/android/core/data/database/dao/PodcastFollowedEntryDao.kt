package com.podlisten.android.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.podlisten.android.core.data.database.model.PodcastFollowedEntry

@Dao
abstract class PodcastFollowedEntryDao : BaseDao<PodcastFollowedEntry> {
    @Query("DELETE FROM podcast_followed_entries WHERE podcast_uri = :podcastUri")
    abstract suspend fun deleteWithPodcastUri(podcastUri: String)

    @Query("SELECT COUNT(*) FROM podcast_followed_entries WHERE podcast_uri = :podcastUri")
    abstract suspend fun podcastFollowedRowCount(podcastUri: String): Int

    suspend fun isPodcastFollowed(podcastUri: String): Boolean {
        return podcastFollowedRowCount(podcastUri) > 0
    }
}