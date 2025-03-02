package com.tungnk123.orpheus.utils

import android.os.Bundle
import androidx.media3.session.SessionCommand

object AppConstants {
    /**
     * Maximum number of entries in the metadata cache.
     * Configured based on average metadata size and typical user session patterns.
     * TODO: Consider making this configurable based on device memory constraints
     */
    const val CACHE_SIZE_CONFIG = 100
    const val SHARING_STARTED_STOP_TIMEOUT = 5_000L
    const val LYRIC_ANIMATE_SCROLL_DURATION = 300L
    const val LYRICS_NOT_FOUND = "LYRICS_NOT_FOUND"


}

object MediaSessionConstants {
    const val ACTION_TOGGLE_LIBRARY = "TOGGLE_LIBRARY"
    const val ACTION_TOGGLE_LIKE = "TOGGLE_LIKE"
    const val ACTION_TOGGLE_SHUFFLE = "TOGGLE_SHUFFLE"
    const val ACTION_TOGGLE_REPEAT_MODE = "TOGGLE_REPEAT_MODE"
    val CommandToggleLibrary = SessionCommand(ACTION_TOGGLE_LIBRARY, Bundle.EMPTY)
    val CommandToggleLike = SessionCommand(ACTION_TOGGLE_LIKE, Bundle.EMPTY)
    val CommandToggleShuffle = SessionCommand(ACTION_TOGGLE_SHUFFLE, Bundle.EMPTY)
    val CommandToggleRepeatMode = SessionCommand(ACTION_TOGGLE_REPEAT_MODE, Bundle.EMPTY)
}