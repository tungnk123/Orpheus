package com.tungnk123.orpheus.data.datasource

import com.tungnk123.orpheus.data.dao.SongDao
import com.tungnk123.orpheus.data.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalSongDataSource @Inject constructor(
    private val songDao: SongDao
) {
    fun observeSongs(): Flow<List<Song>> = songDao.observeSongs()
    fun getSongById(id: String) = songDao.getSongById(songId = id)
    suspend fun insertSong(song: Song) = songDao.insertSong(song)
    suspend fun insertSongs(songs: List<Song>) = songDao.insertSongs(songs)
}