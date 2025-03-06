package com.tungnk123.orpheus.playback.queues

import androidx.media3.common.MediaItem
import com.tungnk123.orpheus.data.model.Song

object EmptyQueue : Queue {
    override val preloadSong: Song? = null

    override suspend fun getInitialStatus() = Queue.Status(
        null,
        emptyList(),
        -1
    )

    override fun hasNextPage() = false

    override suspend fun nextPage() = emptyList<MediaItem>()
}
