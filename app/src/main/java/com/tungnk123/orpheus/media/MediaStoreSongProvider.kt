package com.tungnk123.orpheus.media

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.tungnk123.orpheus.data.model.Song
import com.tungnk123.orpheus.helper.MediaMetadataHelper
import com.tungnk123.orpheus.utils.extensions.toLocalDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStoreSongProvider @Inject constructor(@ApplicationContext private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.S)
    fun getAllSongs(): Flow<List<Song>> = flow {
        val songs = mutableListOf<Song>()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.GENRE
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
            while (cursor.moveToNext()) {
                val song = getSongFromCursor(cursor)
                val metadata = withContext(Dispatchers.IO) {
                    MediaMetadataHelper.getMetadataFromFile(song.path)
                }
                songs.add(
                    song.copy(
                        bitrate = metadata.bitrate,
                        samplingRate = metadata.samplingRate,
                        channels = metadata.channels,
                        discNumber = metadata.discNumber,
                        discTotal = metadata.discTotal,
                        encoder = metadata.encoder,
                        coverFile = metadata.coverFile
                    )
                )
            }
        }
        emit(songs.toList())
    }

    private fun getSongFromCursor(cursor: Cursor): Song {
        val id = cursor.getLong(0).toString()
        val title = cursor.getStringOrNull(1) ?: "Unknown Title"
        val album = cursor.getStringOrNull(2)
        val artist = cursor.getStringOrNull(3)
        val composer = cursor.getStringOrNull(4)
        val year = cursor.getIntOrNull(5)
        val duration = cursor.getLongOrNull(6) ?: 0L
        val path = cursor.getStringOrNull(7) ?: ""
        val dateModified = cursor.getLongOrNull(8)?.times(1000) ?: 0L
        val size = cursor.getLongOrNull(9)
        val albumArtist = cursor.getStringOrNull(10)
        val genre = cursor.getStringOrNull(11)

        val uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

        return Song(
            id = id,
            title = title,
            album = album,
            artists = artist?.split(";")?.toSet() ?: emptySet(),
            composers = composer?.split(";")?.toSet() ?: emptySet(),
            albumArtists = albumArtist?.split(";")?.toSet() ?: emptySet(),
            genres = genre?.split(";")?.toSet() ?: emptySet(),
            date = dateModified.toLocalDate(),
            year = year,
            duration = duration,
            bitrate = null,
            samplingRate = null,
            channels = null,
            encoder = null,
            discNumber = null,
            discTotal = null,
            coverFile = null,
            dateModified = dateModified,
            size = size ?: 0L,
            uri = uri,
            path = path
        )
    }
}