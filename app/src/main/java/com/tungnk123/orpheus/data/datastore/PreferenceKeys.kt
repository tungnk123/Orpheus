package com.tungnk123.orpheus.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val PersistentQueueKey = booleanPreferencesKey("persistentQueue")
val EnableKugouKey = booleanPreferencesKey("enableKugou")
val EnableLrcLibKey = booleanPreferencesKey("enableLrclib")
val PreferredLyricsProviderKey = stringPreferencesKey("lyricsProvider")

enum class PreferredLyricsProvider {
    LRCLIB,
    KUGOU,
}
