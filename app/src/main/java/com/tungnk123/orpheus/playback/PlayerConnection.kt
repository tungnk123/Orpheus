package com.tungnk123.orpheus.playback

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import com.tungnk123.orpheus.data.repository.SongRepository
import com.tungnk123.orpheus.utils.extensions.getCurrentQueueIndex
import com.tungnk123.orpheus.utils.extensions.getQueueWindows
import com.tungnk123.orpheus.utils.extensions.reportException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@androidx.media3.common.util.UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerConnection(
    binder: MusicService.MusicBinder,
    scope: CoroutineScope,
    private val songRepository: SongRepository,
) : Player.Listener {

    companion object {
        private const val TAG = "PlayerConnection"
    }

    val service = binder.service
    val player = service.playerManager.player

    val playbackState = MutableStateFlow(player.playbackState)
    private val playWhenReady = MutableStateFlow(player.playWhenReady)

    val isPlaying =
        combine(
            playbackState,
            playWhenReady
        ) { state, ready ->
            ready && state != STATE_ENDED
        }.stateIn(
            scope,
            SharingStarted.Lazily,
            player.playWhenReady && player.playbackState != STATE_ENDED
        )

    val currentMediaId = MutableStateFlow(player.currentMediaItem?.mediaId)

    val currentSong = currentMediaId.flatMapLatest { mediaId ->
        mediaId?.let { songRepository.getSongById(it) } ?: flowOf(null)
    }

    val currentLyrics = currentMediaId.flatMapLatest { mediaId ->
        mediaId?.let { songRepository.getLyrics(it) } ?: flowOf(null)
    }

    val currentFormat = currentMediaId.flatMapLatest { mediaId ->
        mediaId?.let { songRepository.getFormatById(it) } ?: flowOf(null)
    }

    val queueWindows = MutableStateFlow(player.getQueueWindows())
    val currentMediaItemIndex = MutableStateFlow(player.currentMediaItemIndex)
    val currentWindowIndex = MutableStateFlow(player.getCurrentQueueIndex())

    val shuffleModeEnabled = MutableStateFlow(player.shuffleModeEnabled)
    val repeatMode = MutableStateFlow(player.repeatMode)

    val canSkipPrevious = MutableStateFlow(true)
    val canSkipNext = MutableStateFlow(true)

    val error = MutableStateFlow<PlaybackException?>(null)

    init {
        player.addListener(this)
        updateState()
    }

    private fun updateState() {
        playbackState.value = player.playbackState
        playWhenReady.value = player.playWhenReady
        currentMediaId.value = player.currentMediaItem?.mediaId
        queueTitle.value = service.queueManager.queueTitle
        queueWindows.value = player.getQueueWindows()
        currentWindowIndex.value = player.getCurrentQueueIndex()
        currentMediaItemIndex.value = player.currentMediaItemIndex
        shuffleModeEnabled.value = player.shuffleModeEnabled
        repeatMode.value = player.repeatMode
        updateCanSkipPreviousAndNext()
    }

    fun playQueue(queue: com.tungnk123.orpheus.playback.queues.Queue) {
        service.playQueue(queue)
    }

    fun playNext(item: MediaItem) = playNext(listOf(item))

    fun playNext(items: List<MediaItem>) {
        service.playNext(items)
    }

    fun playNow(mediaItem: MediaItem) {
        Log.d(
            TAG,
            "playNow() called with mediaId=${mediaItem.mediaId}"
        )
        Log.d("PlayerConnection", "mediaItem uri: ${mediaItem.localConfiguration?.uri}")
        player.setMediaItem(mediaItem)

        player.prepare()
        player.playWhenReady = true

        Log.d(
            TAG,
            "Player state after playNow: ${player.playbackState}"
        )
    }


    fun addToQueue(item: MediaItem) = addToQueue(listOf(item))

    fun addToQueue(items: List<MediaItem>) {
        service.addToQueue(items)
    }

    fun toggleLike() {
        service.toggleLike()
    }

    fun seekToNext() {
        player.seekToNext()
        player.prepare()
        player.playWhenReady = true
    }

    fun seekToPrevious() {
        player.seekToPrevious()
        player.prepare()
        player.playWhenReady = true
    }

    override fun onPlaybackStateChanged(state: Int) {
        playbackState.value = state
        error.value = player.playerError
    }

    override fun onPlayWhenReadyChanged(
        newPlayWhenReady: Boolean,
        reason: Int,
    ) {
        playWhenReady.value = newPlayWhenReady
    }

    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int,
    ) {
        currentMediaId.value = mediaItem?.mediaId
        currentMediaItemIndex.value = player.currentMediaItemIndex
        currentWindowIndex.value = player.getCurrentQueueIndex()
        updateCanSkipPreviousAndNext()
    }

    override fun onTimelineChanged(
        timeline: Timeline,
        reason: Int,
    ) {
        queueWindows.value = player.getQueueWindows()
        queueTitle.value = service.queueManager.queueTitle
        currentMediaItemIndex.value = player.currentMediaItemIndex
        currentWindowIndex.value = player.getCurrentQueueIndex()
        updateCanSkipPreviousAndNext()
    }

    override fun onShuffleModeEnabledChanged(enabled: Boolean) {
        shuffleModeEnabled.value = enabled
        queueWindows.value = player.getQueueWindows()
        currentWindowIndex.value = player.getCurrentQueueIndex()
        updateCanSkipPreviousAndNext()
    }

    override fun onRepeatModeChanged(mode: Int) {
        repeatMode.value = mode
        updateCanSkipPreviousAndNext()
    }

    override fun onPlayerErrorChanged(playbackError: PlaybackException?) {
        playbackError?.let {
            reportException(it)
            Log.e(
                TAG,
                "Player error: ${it.errorCodeName} - ${it.message}"
            )
        }
        error.value = playbackError
    }


    private fun updateCanSkipPreviousAndNext() {
        if (!player.currentTimeline.isEmpty) {
            val window = player.currentTimeline.getWindow(
                player.currentMediaItemIndex,
                Timeline.Window()
            )
            canSkipPrevious.value =
                player.isCommandAvailable(COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM) ||
                        !window.isLive ||
                        player.isCommandAvailable(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)

            canSkipNext.value =
                (window.isLive && window.isDynamic) ||
                        player.isCommandAvailable(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
        }
        else {
            canSkipPrevious.value = false
            canSkipNext.value = false
        }
    }

    fun dispose() {
        player.removeListener(this)
    }
}
