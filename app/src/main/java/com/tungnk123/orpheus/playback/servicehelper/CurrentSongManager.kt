package com.tungnk123.orpheus.playback.servicehelper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.media3.common.Player
import androidx.media3.common.Player.EVENT_POSITION_DISCONTINUITY
import androidx.media3.common.Player.EVENT_TIMELINE_CHANGED
import com.tungnk123.orpheus.data.datastore.AudioNormalizationKey
import com.tungnk123.orpheus.data.datastore.ShowLyricsKey
import com.tungnk123.orpheus.data.model.Lyric
import com.tungnk123.orpheus.data.model.Song
import com.tungnk123.orpheus.data.repository.SongRepository
import com.tungnk123.orpheus.lyrics.LyricsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.pow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentSongManager @Inject constructor(
    private val songRepository: SongRepository,
    private val lyricsHelper: LyricsHelper,
    private val dataStore: DataStore<Preferences>
) {
    private val currentSong = MutableStateFlow<Song?>(null)
    
    fun getCurrentSong(): MutableStateFlow<Song?> = currentSong

    fun observeCurrentSong(
        scope: CoroutineScope,
        player: Player,
        normalizeFactor: MutableStateFlow<Float>,
        onSongChanged: () -> Unit
    ) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                if (events.containsAny(EVENT_TIMELINE_CHANGED, EVENT_POSITION_DISCONTINUITY)) {
                    val mediaId = player.currentMediaItem?.mediaId ?: return
                    scope.launch {
                        songRepository.getSongById(mediaId)
                            .collectLatest { song ->
                                currentSong.value = song
                                onSongChanged()
                            }
                    }
                }
            }
        }
        player.addListener(listener)

        scope.launch {
            combine(
                currentSong.distinctUntilChangedBy { it?.id },
                dataStore.data.map { it[ShowLyricsKey] ?: false }.distinctUntilChanged(),
            ) { song, showLyrics ->
                song to showLyrics
            }.collectLatest { (song, showLyrics) ->
                if (showLyrics && song != null && songRepository.getLyrics(song.id) == null) {
                    val lyrics = lyricsHelper.getLyrics(song)
                    songRepository.insertLyrics(
                        Lyric(
                            id = song.id,
                            lyrics = lyrics
                        )
                    )
                }
            }
        }

        scope.launch {
            dataStore.data.map { it[AudioNormalizationKey] ?: true }
                .distinctUntilChanged()
                .collectLatest { normalizeAudio ->
                    currentSong.value?.let {
                        songRepository.getFormatById(it.id)
                            .collectLatest { format ->
                                normalizeFactor.value = if (normalizeAudio && format?.loudnessDb != null) {
                                    min(10f.pow(-format.loudnessDb.toFloat() / 20), 1f)
                                } else {
                                    1f
                                }
                            }
                    }
                }
        }
    }

    fun toggleLike(onSongUpdated: () -> Unit) {
        val song = currentSong.value ?: return
        val updatedSong = song.copy(isFavorite = !song.isFavorite)
        
        CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            songRepository.updateSong(updatedSong)
            currentSong.value = updatedSong
            onSongUpdated()
        }
    }
}