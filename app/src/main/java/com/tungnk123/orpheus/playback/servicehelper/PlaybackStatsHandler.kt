package com.tungnk123.orpheus.playback.servicehelper

import android.database.SQLException
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.analytics.PlaybackStats
import androidx.media3.exoplayer.analytics.PlaybackStatsListener
import com.metrolist.innertube.YouTube
import com.tungnk123.orpheus.data.datastore.HistoryDuration
import com.tungnk123.orpheus.data.datastore.PauseListenHistoryKey
import com.tungnk123.orpheus.data.datastore.get
import com.tungnk123.orpheus.data.model.Event
import com.tungnk123.orpheus.data.repository.SongRepository
import com.tungnk123.orpheus.helper.YTPlayerHelper
import com.tungnk123.orpheus.utils.extensions.reportException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlaybackStatsHandler @Inject constructor(
    private val songRepository: SongRepository,
    private val dataStore: DataStore<Preferences>,
) : PlaybackStatsListener.Callback {

    private lateinit var scope: CoroutineScope

    fun initialize(scope: CoroutineScope) {
        this.scope = scope
    }

    override fun onPlaybackStatsReady(
        eventTime: AnalyticsListener.EventTime,
        playbackStats: PlaybackStats,
    ) {
        val mediaItem = eventTime.timeline.getWindow(
            eventTime.windowIndex,
            Timeline.Window()
        ).mediaItem

        scope.launch(Dispatchers.IO) {
            val mediaId = mediaItem.mediaId

            if (playbackStats.totalPlayTimeMs >= (dataStore[HistoryDuration]?.times(1000f)
                    ?: 30000f) && !dataStore.get(
                    PauseListenHistoryKey,
                    false
                )
            ) {
                try {
                    songRepository.incrementTotalPlayTime(
                        mediaId,
                        playbackStats.totalPlayTimeMs
                    )

                    val event = Event(
                        songId = mediaId,
                        timestamp = LocalDateTime.now(),
                        playTime = playbackStats.totalPlayTimeMs
                    )
                    songRepository.insertEvent(event)

                    val playbackUrl = songRepository.getPlaybackUrl(mediaId)
                        ?: YTPlayerHelper.playerResponseForMetadata(
                            mediaId,
                            null
                        )
                            .getOrNull()?.playbackTracking?.videostatsPlaybackUrl?.baseUrl

                    playbackUrl?.let {
                        YouTube.registerPlayback(
                            null,
                            playbackUrl
                        )
                            .onFailure { reportException(it) }
                    }
                }
                catch (e: SQLException) {
                    reportException(e)
                }
            }
        }
    }
}