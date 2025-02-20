package com.tungnk123.orpheus.data.datasource

import com.tungnk123.orpheus.data.dao.SongDao
import javax.inject.Inject

class LocalSongDataSource @Inject constructor(
    private val songDao: SongDao
) {

}