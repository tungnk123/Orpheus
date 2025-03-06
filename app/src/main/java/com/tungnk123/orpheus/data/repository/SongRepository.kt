package com.tungnk123.orpheus.data.repository

import com.tungnk123.orpheus.data.model.Event
import com.tungnk123.orpheus.data.model.FormatEntity
import com.tungnk123.orpheus.data.model.Lyric
import com.tungnk123.orpheus.data.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun observeSongs(): Flow<List<Song>>
    suspend fun getSongById(id: String): Song
    suspend fun insertSong(song: Song)
    suspend fun insertSongs(songs: List<Song>)
    suspend fun updateSong(song: Song)
    suspend fun incrementTotalPlayTime(
        id: String,
        playTime: Long
    )

    suspend fun insertEvent(event: Event)
    suspend fun getPlaybackUrl(id: String): String?
    suspend fun getLyrics(id: String): Lyric?
    suspend fun insertLyrics(lyricsEntity: Lyric)
    suspend fun getFormatById(id: String): FormatEntity?
}
