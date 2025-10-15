package com.tungnk123.orpheus.playback.servicehelper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.audio.SonicAudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.SilenceSkippingAudioProcessor
import com.tungnk123.orpheus.data.datastore.AudioQuality
import com.tungnk123.orpheus.data.datastore.AudioQualityKey
import com.tungnk123.orpheus.data.datastore.PlayerVolumeKey
import com.tungnk123.orpheus.data.datastore.enumPreferenceAsync
import com.tungnk123.orpheus.data.datastore.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class AudioConfiguration @Inject constructor(
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    val audioQuality by enumPreferenceAsync(
        context,
        AudioQualityKey,
        AudioQuality.AUTO
    )

    val playerVolume = MutableStateFlow(
        dataStore.get(PlayerVolumeKey, 1f).coerceIn(0f, 1f)
    )

    val normalizeFactor = MutableStateFlow(1f)

    fun createAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .build()

    fun createRenderersFactory(): DefaultRenderersFactory = 
        object : DefaultRenderersFactory(context) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean,
            ) = DefaultAudioSink.Builder(context)
                .setEnableFloatOutput(enableFloatOutput)
                .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                .setAudioProcessorChain(
                    DefaultAudioSink.DefaultAudioProcessorChain(
                        emptyArray(),
                        SilenceSkippingAudioProcessor(2_000_000, 20_000, 256),
                        SonicAudioProcessor(),
                    ),
                )
                .build()
        }

    fun observeVolumeChanges(scope: CoroutineScope, onVolumeChange: (Float) -> Unit) {
        scope.launch {
            combine(playerVolume, normalizeFactor) { volume, factor ->
                volume * factor
            }.collectLatest { onVolumeChange(it) }
        }

        scope.launch {
            playerVolume.debounce(1000).collect { volume ->
                dataStore.edit { settings ->
                    settings[PlayerVolumeKey] = volume
                }
            }
        }
    }
}