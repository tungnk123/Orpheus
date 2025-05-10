package com.tungnk123.orpheus.playback

import androidx.compose.runtime.staticCompositionLocalOf

val LocalPlayerConnection =
    staticCompositionLocalOf<PlayerConnection?> { error("No PlayerConnection provided") }
