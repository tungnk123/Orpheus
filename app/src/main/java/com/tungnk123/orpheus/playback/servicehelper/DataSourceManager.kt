package com.tungnk123.orpheus.playback.servicehelper

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.net.toUri
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.metrolist.innertube.YouTube
import com.tungnk123.orpheus.R
import com.tungnk123.orpheus.di.DownloadCache
import com.tungnk123.orpheus.di.PlayerCache
import com.tungnk123.orpheus.helper.YTPlayerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class DataSourceManager @Inject constructor(
    private val context: Context,
    private val connectivityManager: ConnectivityManager,
    @PlayerCache private val playerCache: Cache,
    @DownloadCache private val downloadCache: Cache,
    private val audioConfiguration: AudioConfiguration
) {
    private val songUrlCache = HashMap<String, Pair<String, Long>>()

    companion object {
        const val CHUNK_LENGTH = 512 * 1024L
    }

    fun createCacheDataSource(): CacheDataSource.Factory = CacheDataSource.Factory()
        .setCache(downloadCache)
        .setUpstreamDataSourceFactory(
            CacheDataSource.Factory()
                .setCache(playerCache)
                .setUpstreamDataSourceFactory(
                    DefaultDataSource.Factory(
                        context,
                        OkHttpDataSource.Factory(
                            OkHttpClient.Builder()
                                .proxy(YouTube.proxy)
                                .build(),
                        ),
                    ),
                ),
        )
        .setCacheWriteDataSinkFactory(null)
        .setFlags(FLAG_IGNORE_CACHE_ON_ERROR)

    fun createDataSourceFactory(): DataSource.Factory {
        return ResolvingDataSource.Factory(createCacheDataSource()) { dataSpec ->
            val mediaId = dataSpec.key ?: error("No media id")

            if (downloadCache.isCached(
                    mediaId,
                    dataSpec.position,
                    if (dataSpec.length >= 0) dataSpec.length else 1
                ) || playerCache.isCached(
                    mediaId,
                    dataSpec.position,
                    CHUNK_LENGTH
                )
            ) {
                return@Factory dataSpec
            }

            songUrlCache[mediaId]?.takeIf { it.second > System.currentTimeMillis() }
                ?.let {
                    return@Factory dataSpec.withUri(it.first.toUri())
                }

            val playbackData = runBlocking(Dispatchers.IO) {
                YTPlayerHelper.playerResponseForPlayback(
                    mediaId,
                    playedFormat = null,
                    audioQuality = audioConfiguration.audioQuality.first(),
                    connectivityManager = connectivityManager,
                )
            }.getOrElse { throwable ->
                when (throwable) {
                    is PlaybackException -> throw throwable
                    is ConnectException, is UnknownHostException -> throw PlaybackException(
                        context.getString(R.string.error_no_internet),
                        throwable,
                        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                    )

                    is SocketTimeoutException -> throw PlaybackException(
                        context.getString(R.string.error_timeout),
                        throwable,
                        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT,
                    )

                    else -> throw PlaybackException(
                        context.getString(R.string.error_unknown),
                        throwable,
                        PlaybackException.ERROR_CODE_REMOTE_ERROR,
                    )
                }
            }

            val streamUrl = playbackData.streamUrl
            songUrlCache[mediaId] =
                streamUrl to (System.currentTimeMillis() + (playbackData.streamExpiresInSeconds * 1000L))

            return@Factory dataSpec.withUri(streamUrl.toUri())
                .subrange(
                    dataSpec.uriPositionOffset,
                    CHUNK_LENGTH
                )
        }
    }
}