package com.tungnk123.orpheus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tungnk123.orpheus.data.model.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg playlist: Playlist): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg playlist: Playlist): Int

    @Query("SELECT * FROM Playlists")
    fun observePlaylists(): Flow<List<Playlist>>

    @Query("SELECT * FROM Playlists")
    suspend fun getPlaylists(): List<Playlist>

    @Query("SELECT * FROM Playlists WHERE id =:playlistId")
    suspend fun getPlaylistById(playlistId: String): Playlist

    @Query("DELETE FROM Playlists WHERE id = :playlistId")
    suspend fun deleteById(playlistId: String): Int

    @Query("SELECT * FROM Playlists")
    suspend fun getEntriesPlaylistIDMapped(): List<Playlist>

    @Query("SELECT * FROM Playlists")
    fun observeEntriesPlaylistIDMapped(): Flow<List<Playlist>>
}