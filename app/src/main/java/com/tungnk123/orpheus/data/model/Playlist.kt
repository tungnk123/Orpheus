package com.tungnk123.orpheus.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Playlists")
data class Playlist(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val description: String = "",
    val songPaths: List<String> = emptyList(),
    val uri: Uri? = null,
    val path: String? = null
) {
    val numberOfSongs: Int get() = songPaths.size
    val isLocal get() = uri != null

    fun withTitle(title: String) = copy(title = title)

    companion object {
        private const val PRIMARY_STORAGE = "primary:"

        fun default() = Playlist()
    }
}
