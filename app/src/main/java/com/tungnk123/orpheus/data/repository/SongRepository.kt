package com.tungnk123.orpheus.data.repository

import com.tungnk123.orpheus.data.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun observeSongs(): Flow<List<Song>>
}