package com.tungnk123.orpheus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Lyrics")
data class Lyric(
    @PrimaryKey val id: String,
    val lyrics: String,
) {
    companion object {
        const val LYRICS_NOT_FOUND = "LYRICS_NOT_FOUND"
    }
}
