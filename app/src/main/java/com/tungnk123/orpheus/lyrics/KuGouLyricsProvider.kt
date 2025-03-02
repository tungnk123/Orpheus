package com.tungnk123.orpheus.lyrics

import android.content.Context
import com.metrolist.kugou.KuGou
import com.tungnk123.orpheus.data.datastore.EnableKugouKey
import com.tungnk123.orpheus.data.datastore.dataStore
import com.tungnk123.orpheus.data.datastore.getAsync

object KuGouLyricsProvider : LyricsProvider {
    override val name = "Kugou"
    override suspend fun isEnabled(context: Context): Boolean =
        context.dataStore.getAsync(EnableKugouKey) ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int
    ): Result<String> = KuGou.getLyrics(
        title,
        artist,
        duration
    )

    override suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        callback: (String) -> Unit
    ) {
        KuGou.getAllPossibleLyricsOptions(
            title,
            artist,
            duration,
            callback
        )
    }
}
