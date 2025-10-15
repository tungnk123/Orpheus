import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.PlaybackStatsListener
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.tungnk123.orpheus.MainActivity
import com.tungnk123.orpheus.R
import com.tungnk123.orpheus.di.Dispatcher
import com.tungnk123.orpheus.di.OrpheusDispatchers
import com.tungnk123.orpheus.playback.MediaLibrarySessionCallback
import com.tungnk123.orpheus.playback.PlayerManager
import com.tungnk123.orpheus.playback.queues.Queue
import com.tungnk123.orpheus.playback.servicehelper.AudioConfiguration
import com.tungnk123.orpheus.playback.servicehelper.CurrentSongManager
import com.tungnk123.orpheus.playback.servicehelper.NotificationManager
import com.tungnk123.orpheus.playback.servicehelper.PlaybackStatsHandler
import com.tungnk123.orpheus.playback.servicehelper.QueueManager
import com.tungnk123.orpheus.playback.servicehelper.QueuePersistenceManager
import com.tungnk123.orpheus.playback.servicehelper.SleepTimer
import com.tungnk123.orpheus.utils.CoilBitmapLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.plus
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MusicService : MediaLibraryService(), Player.Listener {

    @Inject lateinit var mediaLibrarySessionCallback: MediaLibrarySessionCallback
    @Inject @Dispatcher(OrpheusDispatchers.Main) lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    lateinit var playerManager: PlayerManager
//    @Inject
//    lateinit var currentSongManager: CurrentSongManager
//    @Inject
//    lateinit var notificationManager: NotificationManager
//    @Inject
//    lateinit var playbackStatsHandler: PlaybackStatsHandler
    @Inject
    lateinit var queueManager: QueueManager
//    @Inject
//    lateinit var queuePersistenceManager: QueuePersistenceManager
//    @Inject
//    lateinit var audioConfiguration: AudioConfiguration


    private lateinit var scope: CoroutineScope
    private val binder = MusicBinder()

//    private val sleepTimer: SleepTimer?
//        get() = playerManager.getSleepTimer()
//
//    private val player: ExoPlayer
//        get() = playerManager.player

    private lateinit var mediaSession: MediaLibrarySession

    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(mainDispatcher) + Job()

        initializeMediaNotificationProvider()
        initializePlayer()
        initializeMediaSession()
        initializeHelpers()
        loadPersistedQueue()
    }

    private fun initializeMediaNotificationProvider() {
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
    }

    private fun initializePlayer() {
//        playerManager.createPlayer(
//            this,
//            scope,
//            this
//        )
    }

    private fun initializeMediaSession() {
        mediaLibrarySessionCallback.apply {
            toggleLike = ::toggleLike
        }

//        mediaSession = MediaLibrarySession.Builder(
//            this,
//            player,
//            mediaLibrarySessionCallback
//        )
//            .setSessionActivity(
//                PendingIntent.getActivity(
//                    this,
//                    0,
//                    Intent(
//                        this,
//                        MainActivity::class.java
//                    ),
//                    PendingIntent.FLAG_IMMUTABLE,
//                ),
//            )
//            .setBitmapLoader(
//                CoilBitmapLoader(
//                    context = this,
//                    scope = scope
//                )
//            )
//            .build()
    }

    private fun initializeHelpers() {
//        playbackStatsHandler.initialize(scope)
//        playerManager.setupVolumeObserver(scope)
//
//        currentSongManager.observeCurrentSong(
//            scope = scope,
//            player = player,
//            normalizeFactor = audioConfiguration.normalizeFactor,
//            onSongChanged = ::updateNotification
//        )
    }

    private fun loadPersistedQueue() {
//        queuePersistenceManager.loadPersistedQueue { queue ->
//            playQueue(
//                queue = queue,
//                playWhenReady = false
//            )
//        }
    }

    private fun updateNotification() {
//        notificationManager.updateNotification(
//            mediaSession = mediaSession,
//            currentSong = currentSongManager.getCurrentSong()
//        )
    }

    fun playNext(items: List<MediaItem>) {
//        queueManager.playNext(
//            player,
//            items
//        )
    }

    fun addToQueue(items: List<MediaItem>) {
//        queueManager.addToQueue(
//            player,
//            items
//        )
    }

    fun playQueue(
        queue: Queue,
        playWhenReady: Boolean = true,
    ) {
//        queueManager.playQueue(
//            player,
//            scope,
//            queue,
//            playWhenReady
//        )
    }

    fun toggleLike() {
//        currentSongManager.toggleLike(::updateNotification)
    }

    override fun onDestroy() {
//        queuePersistenceManager.saveQueueToDisk(
//            player = player,
//            queueTitle = queueManager.queueTitle,
//            automixItems = queueManager.automixItems
//        )
//
//        mediaSession.release()
//        player.removeListener(this)
//        sleepTimer?.let { player.removeListener(it) }
//        playerManager.releasePlayer()
        super.onDestroy()
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
    }
}