package com.tungnk123.orpheus.playback

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.SQLException
import android.net.ConnectivityManager
import android.os.Binder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.EVENT_POSITION_DISCONTINUITY
import androidx.media3.common.Player.EVENT_TIMELINE_CHANGED
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.common.Timeline
import androidx.media3.common.audio.SonicAudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.analytics.PlaybackStats
import androidx.media3.exoplayer.analytics.PlaybackStatsListener
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.SilenceSkippingAudioProcessor
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.mkv.MatroskaExtractor
import androidx.media3.extractor.mp4.FragmentedMp4Extractor
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.metrolist.innertube.YouTube
import com.tungnk123.orpheus.MainActivity
import com.tungnk123.orpheus.R
import com.tungnk123.orpheus.data.datastore.AudioNormalizationKey
import com.tungnk123.orpheus.data.datastore.AudioQuality
import com.tungnk123.orpheus.data.datastore.AudioQualityKey
import com.tungnk123.orpheus.data.datastore.HistoryDuration
import com.tungnk123.orpheus.data.datastore.PauseListenHistoryKey
import com.tungnk123.orpheus.data.datastore.PersistentQueueKey
import com.tungnk123.orpheus.data.datastore.PlayerVolumeKey
import com.tungnk123.orpheus.data.datastore.RepeatModeKey
import com.tungnk123.orpheus.data.datastore.ShowLyricsKey
import com.tungnk123.orpheus.data.datastore.dataStore
import com.tungnk123.orpheus.data.datastore.enumPreferenceAsync
import com.tungnk123.orpheus.data.datastore.get
import com.tungnk123.orpheus.data.model.Event
import com.tungnk123.orpheus.data.model.Lyric
import com.tungnk123.orpheus.data.model.Song
import com.tungnk123.orpheus.data.model.toMediaItem
import com.tungnk123.orpheus.data.repository.SongRepository
import com.tungnk123.orpheus.di.Dispatcher
import com.tungnk123.orpheus.di.DownloadCache
import com.tungnk123.orpheus.di.OrpheusDispatchers
import com.tungnk123.orpheus.di.PlayerCache
import com.tungnk123.orpheus.helper.YTPlayerHelper
import com.tungnk123.orpheus.lyrics.LyricsHelper
import com.tungnk123.orpheus.playback.queues.EmptyQueue
import com.tungnk123.orpheus.playback.queues.ListQueue
import com.tungnk123.orpheus.playback.queues.PersistQueue
import com.tungnk123.orpheus.playback.queues.Queue
import com.tungnk123.orpheus.utils.CoilBitmapLoader
import com.tungnk123.orpheus.utils.MediaSessionConstants.CommandToggleLike
import com.tungnk123.orpheus.utils.extensions.SilentHandler
import com.tungnk123.orpheus.utils.extensions.mediaItems
import com.tungnk123.orpheus.utils.extensions.reportException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.pow

@UnstableApi
@OptIn(
    FlowPreview::class
)
@AndroidEntryPoint
class MusicService @Inject constructor(
    private val songRepository: SongRepository,
    private val lyricsHelper: LyricsHelper,
    private val mediaLibrarySessionCallback: MediaLibrarySessionCallback,
    @Dispatcher(OrpheusDispatchers.Main) val mainDispatcher: CoroutineDispatcher,
    @PlayerCache val playerCache: Cache,
    @DownloadCache val downloadCache: Cache
) : MediaLibraryService(), Player.Listener, PlaybackStatsListener.Callback {

    private var scope = CoroutineScope(mainDispatcher) + Job()
    private val binder = MusicBinder()
    private lateinit var connectivityManager: ConnectivityManager

    private val audioQuality by enumPreferenceAsync(
        this,
        AudioQualityKey,
        AudioQuality.AUTO
    )

    private var currentQueue: Queue = EmptyQueue
    var queueTitle: String? = null

    private val currentSong = MutableStateFlow<Song?>(null)

    private val normalizeFactor = MutableStateFlow(1f)
    val playerVolume = MutableStateFlow(
        dataStore.get(
            PlayerVolumeKey,
            1f
        )
            .coerceIn(
                0f,
                1f
            )
    )

    lateinit var sleepTimer: SleepTimer
    lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaLibrarySession
    private var isAudioEffectSessionOpened = false

    val automixItems = MutableStateFlow<List<MediaItem>>(emptyList())

    override fun onCreate() {
        super.onCreate()
        setMediaNotificationProvider(
            DefaultMediaNotificationProvider(
                this,
                { NOTIFICATION_ID },
                CHANNEL_ID,
                R.string.music_player
            ).apply {
                setSmallIcon(androidx.media3.session.R.drawable.media3_icon_album)
            },
        )

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(createMediaSourceFactory())
            .setRenderersFactory(createRenderersFactory())
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true,
            )
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .build()
            .apply {
                addListener(this@MusicService)
                sleepTimer = SleepTimer(
                    scope,
                    this
                )
                addListener(sleepTimer)
                addAnalyticsListener(
                    PlaybackStatsListener(
                        false,
                        this@MusicService
                    )
                )
            }

        mediaLibrarySessionCallback.apply {
            toggleLike = ::toggleLike
        }

        mediaSession = MediaLibrarySession.Builder(
            this,
            player,
            mediaLibrarySessionCallback
        )
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(
                        this,
                        MainActivity::class.java
                    ),
                    PendingIntent.FLAG_IMMUTABLE,
                ),
            )
            .setBitmapLoader(
                CoilBitmapLoader(
                    context = this,
                    scope = scope
                )
            )
            .build()

        player.repeatMode = dataStore.get(
            RepeatModeKey,
            REPEAT_MODE_OFF
        )

        connectivityManager = getSystemService()!!

        scope.launch {
            combine(
                playerVolume,
                normalizeFactor
            ) { playerVolume, normalizeFactor ->
                playerVolume * normalizeFactor
            }.collectLatest {
                player.volume = it
            }
        }

        scope.launch {
            playerVolume.debounce(1000)
                .collect { volume ->
                    dataStore.edit { settings ->
                        settings[PlayerVolumeKey] = volume
                    }
                }
        }


        player.addListener(object : Player.Listener {
            override fun onEvents(
                player: Player,
                events: Player.Events
            ) {
                if (events.containsAny(
                        EVENT_TIMELINE_CHANGED,
                        EVENT_POSITION_DISCONTINUITY
                    )
                ) {
                    val mediaId = player.currentMediaItem?.mediaId ?: return
                    scope.launch {
                        val song = songRepository.getSongById(mediaId)
                        currentSong.value = song
                        updateNotification()
                    }
                }
            }
        })

        scope.launch {
            combine(
                currentSong.distinctUntilChangedBy { it?.id },
                dataStore.data.map { it[ShowLyricsKey] ?: false }
                    .distinctUntilChanged(),
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
                    val format = currentSong.value?.let { songRepository.getFormatById(it.id) }
                    normalizeFactor.value = if (normalizeAudio && format?.loudnessDb != null) {
                        min(
                            10f.pow(-format.loudnessDb.toFloat() / 20),
                            1f
                        )
                    }
                    else {
                        1f
                    }
                }
        }

        // Khôi phục hàng đợi nếu có
        if (dataStore.get(
                PersistentQueueKey,
                true
            )
        ) {
            runCatching {
                filesDir.resolve(PERSISTENT_QUEUE_FILE)
                    .inputStream()
                    .use { fis ->
                        ObjectInputStream(fis).use { oos ->
                            oos.readObject() as PersistQueue
                        }
                    }
            }.onSuccess { queue ->
                playQueue(
                    queue = ListQueue(
                        title = queue.title,
                        items = queue.items,
                        startIndex = queue.mediaItemIndex,
                        position = queue.position,
                    ),
                    playWhenReady = false,
                )
            }
        }
    }

    override fun onPlaybackStatsReady(
        eventTime: AnalyticsListener.EventTime,
        playbackStats: PlaybackStats,
    ) {
        val mediaItem = eventTime.timeline.getWindow(
            eventTime.windowIndex,
            Timeline.Window()
        ).mediaItem

        scope.launch(Dispatchers.IO) {
            val mediaId = mediaItem.mediaId

            if (playbackStats.totalPlayTimeMs >= (dataStore[HistoryDuration]?.times(1000f)
                    ?: 30000f) && !dataStore.get(
                    PauseListenHistoryKey,
                    false
                )
            ) {
                try {
                    songRepository.incrementTotalPlayTime(
                        mediaId,
                        playbackStats.totalPlayTimeMs
                    )

                    val event = Event(
                        songId = mediaId,
                        timestamp = LocalDateTime.now(),
                        playTime = playbackStats.totalPlayTimeMs
                    )
                    songRepository.insertEvent(event)

                    val playbackUrl = songRepository.getPlaybackUrl(mediaId)
                        ?: YTPlayerHelper.playerResponseForMetadata(
                            mediaId,
                            null
                        )
                            .getOrNull()?.playbackTracking?.videostatsPlaybackUrl?.baseUrl

                    playbackUrl?.let {
                        YouTube.registerPlayback(
                            null,
                            playbackUrl
                        )
                            .onFailure { reportException(it) }
                    }
                }
                catch (e: SQLException) {
                    reportException(e)
                }
            }
        }
    }


    private fun updateNotification() {
        mediaSession.setCustomLayout(
            listOf(
                CommandButton.Builder()
                    .setDisplayName(
                        getString(
                            if (currentSong.value?.isFavorite == true) {
                                R.string.action_remove_like
                            }
                            else {
                                R.string.action_like
                            },
                        ),
                    )
                    .setIconResId(if (currentSong.value?.isFavorite == true) R.drawable.favorite else R.drawable.favorite_border)
                    .setSessionCommand(CommandToggleLike)
                    .setEnabled(currentSong.value != null)
                    .build()
            )
        )
    }

    fun toggleLike() {
        scope.launch {
            currentSong.value?.let { song ->
                songRepository.updateSong(song.copy(isFavorite = !song.isFavorite))
                currentSong.value = song.copy(isFavorite = !song.isFavorite)
                updateNotification()
            }
        }
    }

    private fun createCacheDataSource(): CacheDataSource.Factory = CacheDataSource.Factory()
        .setCache(downloadCache)
        .setUpstreamDataSourceFactory(
            CacheDataSource.Factory()
                .setCache(playerCache)
                .setUpstreamDataSourceFactory(
                    DefaultDataSource.Factory(
                        this,
                        OkHttpDataSource.Factory(
                            OkHttpClient.Builder()
                                .proxy(YouTube.proxy)
                                .build(),
                        ),
                    ),
                ),
        )
        .setCacheWriteDataSinkFactory(null)
        .setFlags(FLAG_IGNORE_CACHE_ON_ERROR)

    private fun createDataSourceFactory(): DataSource.Factory {
        val songUrlCache = HashMap<String, Pair<String, Long>>()

        return ResolvingDataSource.Factory(createCacheDataSource()) { dataSpec ->
            val mediaId = dataSpec.key ?: error("No media id")

            if (downloadCache.isCached(
                    mediaId,
                    dataSpec.position,
                    if (dataSpec.length >= 0) dataSpec.length else 1
                ) || playerCache.isCached(
                    mediaId,
                    dataSpec.position,
                    CHUNK_LENGTH
                )
            ) {
                return@Factory dataSpec
            }

            songUrlCache[mediaId]?.takeIf { it.second > System.currentTimeMillis() }
                ?.let {
                    return@Factory dataSpec.withUri(it.first.toUri())
                }

            val playbackData = runBlocking(Dispatchers.IO) {
                YTPlayerHelper.playerResponseForPlayback(
                    mediaId,
                    playedFormat = null,
                    audioQuality = audioQuality.first(),
                    connectivityManager = connectivityManager,
                )
            }.getOrElse { throwable ->
                when (throwable) {
                    is PlaybackException -> throw throwable
                    is ConnectException, is UnknownHostException -> throw PlaybackException(
                        getString(R.string.error_no_internet),
                        throwable,
                        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                    )

                    is SocketTimeoutException -> throw PlaybackException(
                        getString(R.string.error_timeout),
                        throwable,
                        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT,
                    )

                    else -> throw PlaybackException(
                        getString(R.string.error_unknown),
                        throwable,
                        PlaybackException.ERROR_CODE_REMOTE_ERROR,
                    )
                }
            }

            val streamUrl = playbackData.streamUrl
            songUrlCache[mediaId] =
                streamUrl to (System.currentTimeMillis() + (playbackData.streamExpiresInSeconds * 1000L))

            return@Factory dataSpec.withUri(streamUrl.toUri())
                .subrange(
                    dataSpec.uriPositionOffset,
                    CHUNK_LENGTH
                )
        }
    }


    private fun createMediaSourceFactory() = DefaultMediaSourceFactory(
        createDataSourceFactory(),
    ) {
        arrayOf(
            MatroskaExtractor(),
            FragmentedMp4Extractor()
        )
    }

    private fun createRenderersFactory() = object : DefaultRenderersFactory(this) {
        override fun buildAudioSink(
            context: Context,
            enableFloatOutput: Boolean,
            enableAudioTrackPlaybackParams: Boolean,
        ) = DefaultAudioSink.Builder(this@MusicService)
            .setEnableFloatOutput(enableFloatOutput)
            .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
            .setAudioProcessorChain(
                DefaultAudioSink.DefaultAudioProcessorChain(
                    emptyArray(),
                    SilenceSkippingAudioProcessor(
                        2_000_000,
                        20_000,
                        256
                    ),
                    SonicAudioProcessor(),
                ),
            )
            .build()
    }

    fun playQueue(
        queue: Queue,
        playWhenReady: Boolean = true,
    ) {
        if (!scope.isActive) scope = CoroutineScope(Dispatchers.Main) + Job()
        currentQueue = queue
        queueTitle = null
        player.shuffleModeEnabled = false
        queue.preloadSong?.let { song ->
            player.setMediaItem(song.toMediaItem())
            player.prepare()
            player.playWhenReady = playWhenReady
        }
        if (queue.preloadSong != null) {

        }
        scope.launch(SilentHandler) {
            val initialStatus = withContext(Dispatchers.IO) {
                queue.getInitialStatus()
            }
            if (queue.preloadSong != null && player.playbackState == STATE_IDLE) return@launch
            if (initialStatus.title != null) {
                queueTitle = initialStatus.title
            }
            if (initialStatus.items.isEmpty()) return@launch
            if (queue.preloadSong != null) {
                player.addMediaItems(
                    0,
                    initialStatus.items.subList(
                        0,
                        initialStatus.mediaItemIndex
                    )
                )
                player.addMediaItems(
                    initialStatus.items.subList(
                        initialStatus.mediaItemIndex + 1,
                        initialStatus.items.size
                    )
                )
            }
            else {
                player.setMediaItems(
                    initialStatus.items,
                    if (initialStatus.mediaItemIndex > 0) {
                        initialStatus.mediaItemIndex
                    }
                    else {
                        0
                    },
                    initialStatus.position,
                )
                player.prepare()
                player.playWhenReady = playWhenReady
            }
        }
    }

    override fun onDestroy() {
        if (dataStore.get(
                PersistentQueueKey,
                true
            )
        ) {
            saveQueueToDisk()
        }
        mediaSession.release()
        player.removeListener(this)
        player.removeListener(sleepTimer)
        player.release()
        super.onDestroy()
    }

    private fun saveQueueToDisk() {
        if (player.playbackState == STATE_IDLE) {
            filesDir.resolve(PERSISTENT_AUTOMIX_FILE)
                .delete()
            filesDir.resolve(PERSISTENT_QUEUE_FILE)
                .delete()
            return
        }
        val persistQueue = PersistQueue(
            title = queueTitle,
            items = player.mediaItems,
            mediaItemIndex = player.currentMediaItemIndex,
            position = player.currentPosition,
        )
        val persistAutomix = PersistQueue(
            title = "automix",
            items = automixItems.value,
            mediaItemIndex = 0,
            position = 0,
        )
        runCatching {
            filesDir.resolve(PERSISTENT_QUEUE_FILE)
                .outputStream()
                .use { fos ->
                    ObjectOutputStream(fos).use { oos ->
                        oos.writeObject(persistQueue)
                    }
                }
        }.onFailure {
            reportException(it)
        }
        runCatching {
            filesDir.resolve(PERSISTENT_AUTOMIX_FILE)
                .outputStream()
                .use { fos ->
                    ObjectOutputStream(fos).use { oos ->
                        oos.writeObject(persistAutomix)
                    }
                }
        }.onFailure {
            reportException(it)
        }
    }

    override fun onBind(intent: Intent?) = super.onBind(intent) ?: binder

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    companion object {
        const val ROOT = "root"
        const val SONG = "song"
        const val ARTIST = "artist"
        const val ALBUM = "album"
        const val PLAYLIST = "playlist"

        const val CHANNEL_ID = "music_channel_01"
        const val NOTIFICATION_ID = 888
        const val ERROR_CODE_NO_STREAM = 1000001
        const val CHUNK_LENGTH = 512 * 1024L
        const val PERSISTENT_QUEUE_FILE = "persistent_queue.data"
        const val PERSISTENT_AUTOMIX_FILE = "persistent_automix.data"
    }
}

