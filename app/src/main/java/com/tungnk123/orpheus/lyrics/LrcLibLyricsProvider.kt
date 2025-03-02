package com.tungnk123.orpheus.lyrics

import android.content.Context
import com.metrolist.lrclib.LrcLib
import com.tungnk123.orpheus.data.datastore.EnableLrcLibKey
import com.tungnk123.orpheus.data.datastore.dataStore
import com.tungnk123.orpheus.data.datastore.getAsync

object LrcLibLyricsProvider : LyricsProvider {
    override val name = "LrcLib"

    override suspend fun isEnabled(context: Context): Boolean =
        context.dataStore.getAsync(EnableLrcLibKey) ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
    ): Result<String> = LrcLib.getLyrics(
        title,
        artist,
        duration
    )

    override suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        callback: (String) -> Unit,
    ) {
        LrcLib.getAllLyrics(
            title,
            artist,
            duration,
            null,
            callback
        )
    }
}
