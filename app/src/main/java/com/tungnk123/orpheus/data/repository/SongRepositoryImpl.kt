package com.tungnk123.orpheus.data.repository

import com.tungnk123.orpheus.data.datasource.LocalSongDataSource
import com.tungnk123.orpheus.data.model.Event
import com.tungnk123.orpheus.data.model.FormatEntity
import com.tungnk123.orpheus.data.model.Lyric
import com.tungnk123.orpheus.data.model.Song
import com.tungnk123.orpheus.di.Dispatcher
import com.tungnk123.orpheus.di.OrpheusDispatchers
import com.tungnk123.orpheus.utils.extensions.printLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val localSongDataSource: LocalSongDataSource,
    @Dispatcher(OrpheusDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : SongRepository {

    override fun observeSongs(): Flow<List<Song>> {
        val temp = localSongDataSource.observeSongs()
        temp.toString().printLog(tag = TAG)
        return temp
    }

    override suspend fun getSongById(id: String) = localSongDataSource.getSongById(id)

    override suspend fun insertSong(song: Song) {
        localSongDataSource.insertSong(song)
    }

    override suspend fun insertSongs(songs: List<Song>) {
        localSongDataSource.insertSongs(songs)
    }

    override suspend fun updateSong(song: Song) {
        TODO("Not yet implemented")
    }

    override suspend fun incrementTotalPlayTime(
        id: String,
        playTime: Long
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun insertEvent(event: Event) {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaybackUrl(id: String): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getLyrics(id: String): Lyric? {
        TODO("Not yet implemented")
    }

    override suspend fun insertLyrics(lyricsEntity: Lyric) {
        TODO("Not yet implemented")
    }

    override suspend fun getFormatById(id: String): FormatEntity? {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "SongRepositoryImpl"
    }
}