package com.tungnk123.orpheus.data.model

import androidx.room.Entity
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@Entity("Albums")
data class Album(
    val id: String = "",
    val name: String = "",
    val artists: MutableSet<String> = mutableSetOf(),
    var startYear: Int? = null,
    var endYear: Int? = null,
    var numberOfSongs: Int = 0,
    var duration: Duration = Duration.ZERO,
)