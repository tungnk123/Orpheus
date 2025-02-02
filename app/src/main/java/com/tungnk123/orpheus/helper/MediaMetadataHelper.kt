package com.tungnk123.orpheus.helper

import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.annotation.RequiresApi
import com.tungnk123.orpheus.data.model.MetadataInfo
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object MediaMetadataHelper {
    @RequiresApi(Build.VERSION_CODES.S)
    fun getMetadataFromFile(filePath: String): MetadataInfo {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(filePath)
            MetadataInfo(
                title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                albumArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST),
                genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE),
                year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)
                    ?.toIntOrNull(),
                date = extractDateFromFile(filePath),
                duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull(),
                trackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
                    ?.toIntOrNull(),
                totalTracks = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)
                    ?.toIntOrNull(),
                discNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)
                    ?.toIntOrNull(),
                discTotal = null,
                bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                    ?.toLongOrNull(),
                samplingRate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)
                    ?.toLongOrNull(),
                channels = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)
                    ?.toIntOrNull(),
                encoder = null,
                coverFile = extractCoverFile(retriever)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            MetadataInfo()
        } finally {
            retriever.release()
        }
    }

    private fun extractCoverFile(retriever: MediaMetadataRetriever): String? {
        return try {
            val picture = retriever.embeddedPicture
            if (picture != null) "data:image/jpeg;base64,${
                android.util.Base64.encodeToString(picture, android.util.Base64.DEFAULT)
            }" else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun extractDateFromFile(filePath: String): LocalDate? {
        return try {
            val file = File(filePath)
            if (file.exists()) {
                Instant.ofEpochMilli(file.lastModified())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
