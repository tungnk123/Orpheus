package com.tungnk123.orpheus.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.tungnk123.orpheus.data.datasource.LocalSongDataSource
import com.tungnk123.orpheus.data.model.Song
import com.tungnk123.orpheus.di.Dispatcher
import com.tungnk123.orpheus.di.OrpheusDispatchers
import com.tungnk123.orpheus.media.MediaStoreSongProvider
import com.tungnk123.orpheus.utils.extensions.printLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val localSongDataSource: LocalSongDataSource,
    @Dispatcher(OrpheusDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val mediaStoreSongProvider: MediaStoreSongProvider
) : SongRepository {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun observeSongs(): Flow<List<Song>> {
        val temp = mediaStoreSongProvider.getAllSongs()
        temp.toString().printLog(tag = TAG)
        return temp
    }

    companion object {
        private const val TAG = "SongRepositoryImpl"
    }
}