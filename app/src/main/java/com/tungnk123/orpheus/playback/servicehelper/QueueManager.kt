package com.tungnk123.orpheus.playback.servicehelper

import androidx.media3.common.MediaItem
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.exoplayer.ExoPlayer
import com.tungnk123.orpheus.data.model.toMediaItem
import com.tungnk123.orpheus.playback.queues.EmptyQueue
import com.tungnk123.orpheus.playback.queues.Queue
import com.tungnk123.orpheus.utils.extensions.SilentHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueManager @Inject constructor() {
    private var currentQueue: Queue = EmptyQueue
    var queueTitle: String? = null
    val automixItems = MutableStateFlow<List<MediaItem>>(emptyList())

    fun playQueue(
        player: ExoPlayer,
        scope: CoroutineScope,
        queue: Queue,
        playWhenReady: Boolean = true,
    ) {
        currentQueue = queue
        queueTitle = null
        player.shuffleModeEnabled = false

        queue.preloadSong?.let { song ->
            player.setMediaItem(song.toMediaItem())
            player.prepare()
            player.playWhenReady = playWhenReady
        }

        scope.launch(SilentHandler) {
            val initialStatus = withContext(Dispatchers.IO) {
                queue.getInitialStatus()
            }

            if (queue.preloadSong != null && player.playbackState == STATE_IDLE) return@launch

            if (initialStatus.title != null) {
                queueTitle = initialStatus.title
            }

            if (initialStatus.items.isEmpty()) return@launch

            if (queue.preloadSong != null) {
                player.addMediaItems(
                    0,
                    initialStatus.items.subList(
                        0,
                        initialStatus.mediaItemIndex
                    )
                )
                player.addMediaItems(
                    initialStatus.items.subList(
                        initialStatus.mediaItemIndex + 1,
                        initialStatus.items.size
                    )
                )
            }
            else {
                player.setMediaItems(
                    initialStatus.items,
                    if (initialStatus.mediaItemIndex > 0) {
                        initialStatus.mediaItemIndex
                    }
                    else {
                        0
                    },
                    initialStatus.position,
                )
                player.prepare()
                player.playWhenReady = playWhenReady
            }
        }
    }

    fun playNext(
        player: ExoPlayer,
        items: List<MediaItem>,
    ) {
        val currentIndex = player.currentMediaItemIndex
        if (currentIndex >= 0) {
            player.addMediaItems(
                currentIndex + 1,
                items
            )
        }
        else {
            player.setMediaItems(items)
            player.prepare()
            player.playWhenReady = true
        }
    }

    fun addToQueue(
        player: ExoPlayer,
        items: List<MediaItem>,
    ) {
        player.addMediaItems(items)
    }

    fun getCurrentQueue(): Queue = currentQueue
}