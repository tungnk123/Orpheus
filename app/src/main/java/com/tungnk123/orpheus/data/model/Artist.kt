package com.tungnk123.orpheus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity("Artists")
data class Artist(
    @PrimaryKey val id: String,
    val name: String = "",
    var numberOfAlbums: Int = 0,
    var numberOfSongs: Int = 0,
)