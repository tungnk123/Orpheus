package com.tungnk123.orpheus.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tungnk123.orpheus.data.dao.PlaylistDao
import com.tungnk123.orpheus.data.dao.SongDao
import com.tungnk123.orpheus.data.model.Album
import com.tungnk123.orpheus.data.model.Artist
import com.tungnk123.orpheus.data.model.Playlist
import com.tungnk123.orpheus.data.model.Song
import com.tungnk123.orpheus.data.converter.RoomConverter

@Database(
    entities = [Album::class, Artist::class, Song::class, Playlist::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DB_NAME = "orpheus_db"
    }

    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
}