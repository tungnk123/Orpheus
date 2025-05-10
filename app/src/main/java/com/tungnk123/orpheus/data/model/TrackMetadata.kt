package com.tungnk123.orpheus.data.model

data class TrackMetadata(
    val id: String,
    val title: String,
    val artists: List<Artist>,
    val duration: Long,
    val thumbnailUrl: String? = null,
    val album: Album? = null,
    val setVideoId: String? = null,
    val explicit: Boolean = false,
)

