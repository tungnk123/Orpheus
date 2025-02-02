package com.tungnk123.orpheus.data.model

import java.time.LocalDate

data class MetadataInfo(
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val albumArtist: String? = null,
    val genre: String? = null,
    val year: Int? = null,
    val date: LocalDate? = null,
    val duration: Long? = null,
    val trackNumber: Int? = null,
    val totalTracks: Int? = null,
    val discNumber: Int? = null,
    val discTotal: Int? = null,
    val bitrate: Long? = null,
    val samplingRate: Long? = null,
    val channels: Int? = null,
    val encoder: String? = null,
    val coverFile: String? = null
)
