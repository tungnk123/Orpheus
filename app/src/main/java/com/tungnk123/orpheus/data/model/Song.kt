package com.tungnk123.orpheus.data.model

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tungnk123.orpheus.helper.AssetsHelper
import com.tungnk123.orpheus.helper.AssetsHelper.createHandyImageRequest
import java.time.LocalDate

@Entity(tableName = "Songs")
data class Song(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val album: String? = null,
    val isFavorite: Boolean = false,
    val artists: Set<String> = emptySet(),
    val composers: Set<String> = emptySet(),
    val albumArtists: Set<String> = emptySet(),
    val genres: Set<String> = emptySet(),
    val discNumber: Int? = null,
    val discTotal: Int? = null,
    val date: LocalDate? = null,
    val year: Int? = null,
    val duration: Long = 0L,
    val bitrate: Long? = null,
    val samplingRate: Long? = null,
    val channels: Int? = null,
    val encoder: String? = null,
    val dateModified: Long = 0L,
    val size: Long = 0L,
    val coverFile: String? = null,
    val uri: Uri = Uri.EMPTY,
    val path: String = ""
) {
    fun createArtworkImageRequest(
        context: Context,
    ) = createHandyImageRequest(
        context = context,
        image = coverFile ?: AssetsHelper.placeholderLightId,
        fallback = AssetsHelper.getPlaceholderId(isLight = true),
    ).build()

}

@OptIn(UnstableApi::class)
fun Song.toMediaItem() =
    MediaItem
        .Builder()
        .setMediaId(this.id)
        .setUri(this.uri)
        .setCustomCacheKey(this.id)
        .setTag(this)
        .setMediaMetadata(
            MediaMetadata
                .Builder()
                .setTitle(this.title)
                .setSubtitle(artists.joinToString())
                .setArtist(artists.joinToString())
                .setAlbumTitle(this.album)
                .setArtworkUri(this.coverFile?.toUri())
                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                .setIsPlayable(true)
                .setIsBrowsable(false)
                .build()
        )
        .build()


