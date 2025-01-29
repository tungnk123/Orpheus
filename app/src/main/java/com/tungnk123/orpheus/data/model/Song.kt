package com.tungnk123.orpheus.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Songs")
data class Song(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val description: String = "",
    val album: String? = null,
    val artists: Set<String> = emptySet(),
    val composers: Set<String> = emptySet(),
    val albumArtists: Set<String> = emptySet(),
    val genres: Set<String> = emptySet(),
    val numberOfSongs: Int? = null,
    val totalSong: Int? = null,
    val discNumber: Int? = null,
    val discTotal: Int? = null,
    val date: LocalDate? = null,
    val year: Int? = null,
    val duration: Long = 0L,
    val bitrate: Long? = null,
    val samplingRate: Long? = null,
    val channels: Int? = null,
    val encoder: String? = null,
    val dateModified: Long = 0L,
    val size: Long = 0L,
    val coverFile: String? = null,
    val uri: Uri = Uri.EMPTY,
    val path: String = ""
)
