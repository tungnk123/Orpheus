package com.tungnk123.orpheus.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val PersistentQueueKey = booleanPreferencesKey("persistentQueue")
val EnableKugouKey = booleanPreferencesKey("enableKugou")
val EnableLrcLibKey = booleanPreferencesKey("enableLrclib")
val PreferredLyricsProviderKey = stringPreferencesKey("lyricsProvider")
val PlayerVolumeKey = floatPreferencesKey("playerVolume")
val RepeatModeKey = intPreferencesKey("repeatMode")
val MaxSongCacheSizeKey = intPreferencesKey("maxSongCacheSize")
val ShowLyricsKey = booleanPreferencesKey("showLyrics")
val AudioNormalizationKey = booleanPreferencesKey("audioNormalization")
val PauseListenHistoryKey = booleanPreferencesKey("pauseListenHistory")
val HistoryDuration = floatPreferencesKey("historyDuration")
val HideExplicitKey = booleanPreferencesKey("hideExplicit")

enum class PreferredLyricsProvider {
    LRCLIB,
    KUGOU,
}

val AudioQualityKey = stringPreferencesKey("audioQuality")

enum class AudioQuality {
    AUTO,
    MAX,
    HIGH,
    LOW,
}
