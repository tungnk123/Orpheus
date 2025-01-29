package com.tungnk123.orpheus.data.model

import kotlinx.serialization.Serializable


@Serializable
data class AlbumArtist(
    val name: String = "",
    var numberOfAlbums: Int = 0,
    var numberOfSongs: Int = 0,
) 