package com.tungnk123.orpheus.data.model

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity("Genres")
data class Genre(
    val name: String = "",
    var numberOfSongs: Int = 0,
) 