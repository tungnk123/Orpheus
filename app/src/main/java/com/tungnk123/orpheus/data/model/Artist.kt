package com.tungnk123.orpheus.data.model

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity("Artists")
data class Artist(
    val name: String = "",
    val description: String = "",
    var numberOfAlbums: Int = 0,
    var numberOfSongs: Int = 0,
)