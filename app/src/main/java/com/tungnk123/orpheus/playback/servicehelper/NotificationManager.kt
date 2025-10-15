package com.tungnk123.orpheus.playback.servicehelper

import android.content.Context
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import com.tungnk123.orpheus.R
import com.tungnk123.orpheus.data.model.Song
import com.tungnk123.orpheus.utils.MediaSessionConstants.CommandToggleLike
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    private val context: Context
) {
    fun updateNotification(
        mediaSession: MediaSession,
        currentSong: MutableStateFlow<Song?>
    ) {
        val song = currentSong.value
        mediaSession.setCustomLayout(
            listOf(
                CommandButton.Builder()
                    .setDisplayName(
                        context.getString(
                            if (song?.isFavorite == true) {
                                R.string.action_remove_like
                            } else {
                                R.string.action_like
                            },
                        ),
                    )
                    .setIconResId(
                        if (song?.isFavorite == true) {
                            R.drawable.favorite
                        } else {
                            R.drawable.favorite_border
                        }
                    )
                    .setSessionCommand(CommandToggleLike)
                    .setEnabled(song != null)
                    .build()
            )
        )
    }
}