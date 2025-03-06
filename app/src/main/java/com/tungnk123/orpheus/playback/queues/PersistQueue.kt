package com.tungnk123.orpheus.playback.queues

import androidx.media3.common.MediaItem
import java.io.Serializable

data class PersistQueue(
    val title: String?,
    val items: List<MediaItem>,
    val mediaItemIndex: Int,
    val position: Long,
) : Serializable
