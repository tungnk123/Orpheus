package com.tungnk123.orpheus.playback.queues

import androidx.media3.common.MediaItem
import com.tungnk123.orpheus.data.model.Song

class ListQueue(
    val title: String? = null,
    val items: List<MediaItem>,
    val startIndex: Int = 0,
    val position: Long = 0L,
) : Queue {
    override val preloadSong: Song? = null

    override suspend fun getInitialStatus() = Queue.Status(
        title,
        items,
        startIndex,
        position
    )

    override fun hasNextPage(): Boolean = false

    override suspend fun nextPage() = throw UnsupportedOperationException()
}
