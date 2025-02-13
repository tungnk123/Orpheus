package com.tungnk123.orpheus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tungnk123.orpheus.data.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg song: Song): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg song: Song): Int

    @Query("SELECT * FROM Songs")
    fun observeSongs(): Flow<List<Song>>

    @Query("SELECT * FROM Songs")
    fun getSongs(): List<Song>

    @Query("SELECT * FROM Songs WHERE id = :songId")
    suspend fun getSongById(songId: String): Song

    @Query("DELETE FROM Songs WHERE id = :songId")
    suspend fun deleteById(songId: String): Int

    @Query("DELETE FROM Songs WHERE id IN (:songIds)")
    suspend fun deleteByIds(songIds: Collection<String>): Int

    @Query("DELETE FROM Songs")
    suspend fun clearAllSongs(): Int

    @Query("SELECT * FROM songs")
    suspend fun getEntriesPathMapped(): List<Song>

    @Query("SELECT * FROM songs")
    fun observeEntriesPathMapped(): Flow<List<Song>>
}