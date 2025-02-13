package com.tungnk123.orpheus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@Entity("Albums")
data class Album(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val artists: Set<String> = emptySet(),
    var startYear: Int? = null,
    var endYear: Int? = null,
    var numberOfSongs: Int = 0,
    var duration: Int? = null,
)