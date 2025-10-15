package com.tungnk123.orpheus.playback

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.PlaybackStatsListener
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.tungnk123.orpheus.data.datastore.RepeatModeKey
import com.tungnk123.orpheus.data.datastore.dataStore
import com.tungnk123.orpheus.data.datastore.get
import com.tungnk123.orpheus.playback.servicehelper.AudioConfiguration
import com.tungnk123.orpheus.playback.servicehelper.DataSourceManager
import com.tungnk123.orpheus.playback.servicehelper.PlaybackStatsHandler
import com.tungnk123.orpheus.playback.servicehelper.SleepTimer
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    private val audioConfiguration: AudioConfiguration,
    private val dataSourceManager: DataSourceManager,
    private val playbackStatsHandler: PlaybackStatsHandler
) {

    private var _player: ExoPlayer? = null
    val player: ExoPlayer
        get() = _player ?: throw IllegalStateException("Player not initialized")

    private var sleepTimer: SleepTimer? = null

    fun createPlayer(
        context: Context,
        scope: CoroutineScope,
        playerListener: Player.Listener? = null
    ): ExoPlayer {
        if (_player != null) {
            releasePlayer()
        }

        _player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(createMediaSourceFactory())
            .setRenderersFactory(audioConfiguration.createRenderersFactory())
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setAudioAttributes(audioConfiguration.createAudioAttributes(), true)
            .setSeekBackIncrementMs(SEEK_BACK_INCREMENT_MS)
            .setSeekForwardIncrementMs(SEEK_FORWARD_INCREMENT_MS)
            .build()
            .apply {
                // Set repeat mode from datastore
                repeatMode = context.dataStore.get(RepeatModeKey, REPEAT_MODE_OFF)

                // Add listeners
                playerListener?.let { addListener(it) }

                // Initialize and add sleep timer
                sleepTimer = SleepTimer(scope, this).also { timer ->
                    addListener(timer)
                }

                // Add analytics listener
                addAnalyticsListener(PlaybackStatsListener(false, playbackStatsHandler))
            }

        return player
    }

    fun setupVolumeObserver(scope: CoroutineScope) {
        audioConfiguration.observeVolumeChanges(scope) { volume ->
            _player?.volume = volume
        }
    }

    fun updateVolume(volume: Float) {
        _player?.volume = volume
    }

    fun getSleepTimer(): SleepTimer? = sleepTimer

    fun releasePlayer() {
        _player?.let { player ->
            sleepTimer?.let { timer ->
                player.removeListener(timer)
            }
            player.release()
        }
        _player = null
        sleepTimer = null
    }

    fun isPlayerInitialized(): Boolean = _player != null

    private fun createMediaSourceFactory(): DefaultMediaSourceFactory {
        return DefaultMediaSourceFactory(dataSourceManager.createDataSourceFactory())
    }

    companion object {
        private const val SEEK_BACK_INCREMENT_MS = 5000L
        private const val SEEK_FORWARD_INCREMENT_MS = 5000L
    }
}