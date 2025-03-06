package com.tungnk123.orpheus.playback.queues

import androidx.media3.common.MediaItem
import com.tungnk123.orpheus.data.model.Song

interface Queue {
    val preloadSong: Song?

    suspend fun getInitialStatus(): Status

    fun hasNextPage(): Boolean

    suspend fun nextPage(): List<MediaItem>

    data class Status(
        val title: String?,
        val items: List<MediaItem>,
        val mediaItemIndex: Int,
        val position: Long = 0L,
    )
}
