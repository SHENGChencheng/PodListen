package com.podlisten.android.core.data.network

import coil.network.HttpException
import com.podlisten.android.core.data.Dispatcher
import com.podlisten.android.core.data.PodListenDispatcher
import com.podlisten.android.core.data.database.model.Category
import com.podlisten.android.core.data.database.model.Episode
import com.podlisten.android.core.data.database.model.Podcast
import com.rometools.modules.itunes.EntryInformation
import com.rometools.modules.itunes.FeedInformation
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PodcastsFetcher @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val syndFeedInput: SyndFeedInput,
    @Dispatcher(PodListenDispatcher.IO) private val ioDispatcher: CoroutineDispatcher
) {
    private val cacheControl by lazy {
        CacheControl.Builder().maxStale(8, TimeUnit.HOURS).build()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(feedUrls: List<String>): Flow<PodcastRssResponse> {
        return feedUrls.asFlow()
            .flatMapMerge { feedUrl ->
                flow {
                    emit(fetchPodcast(feedUrl))
                }.catch { e ->
                    emit(PodcastRssResponse.Error(e))
                }
            }
    }

    private suspend fun fetchPodcast(url: String): PodcastRssResponse {
        return withContext(ioDispatcher) {
            val request = Request.Builder()
                .url(url)
                .cacheControl(cacheControl)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) throw HttpException(response)

            response.body!!.use { body ->
                syndFeedInput.build(body.charStream()).toPodcastResponse(url)
            }
        }
    }
}

sealed class PodcastRssResponse {
    data class Error(
        val throwable: Throwable?,
    ) : PodcastRssResponse()

    data class Success(
        val podcast: Podcast,
        val episodes: List<Episode>,
        val categories: Set<Category>
    ) : PodcastRssResponse()
}

private fun SyndFeed.toPodcastResponse(feedUrl: String): PodcastRssResponse {
    val podcastUri = uri ?: feedUrl
    val episodes = entries.map { it.toEpisode(podcastUri) }

    val feedInfo = getModule(PodcastModuleDtd) as? FeedInformation
    val podcast = Podcast(
        uri = podcastUri,
        title = title,
        description = feedInfo?.summary ?: description,
        author = author,
        copyright = copyright,
        imageUrl = feedInfo?.imageUri?.toString()
    )

    val categories = feedInfo?.categories
        ?.map { Category(name = it.name) }
        ?.toSet() ?: emptySet()

    return PodcastRssResponse.Success(podcast, episodes, categories)
}

private fun SyndEntry.toEpisode(podcastUri: String): Episode {
    val entryInformation = getModule(PodcastModuleDtd) as? EntryInformation
    return Episode(
        uri = uri,
        podcastUri = podcastUri,
        title = title,
        author = author,
        summary = entryInformation?.summary ?: description?.value,
        subtitle = entryInformation?.subtitle,
        published = Instant.ofEpochMilli(publishedDate.time).atOffset(ZoneOffset.UTC),
        duration = entryInformation?.duration?.milliseconds?.let { Duration.ofMillis(it) }
    )
}

/**
 * Most feeds use the following DTD to include extra information related to
 * their podcast. Info such as images, summaries, duration, categories is sometimes only available
 * via this attributes in this DTD.
 */
private const val PodcastModuleDtd = "http://www.itunes.com/dtds/podcast-1.0.dtd"