package com.tungnk123.orpheus.playback.servicehelper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.media3.common.MediaItem
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.exoplayer.ExoPlayer
import com.tungnk123.orpheus.data.datastore.PersistentQueueKey
import com.tungnk123.orpheus.data.datastore.get
import com.tungnk123.orpheus.playback.queues.ListQueue
import com.tungnk123.orpheus.playback.queues.PersistQueue
import com.tungnk123.orpheus.utils.extensions.mediaItems
import com.tungnk123.orpheus.utils.extensions.reportException
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueuePersistenceManager @Inject constructor(
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        const val PERSISTENT_QUEUE_FILE = "persistent_queue.data"
        const val PERSISTENT_AUTOMIX_FILE = "persistent_automix.data"
    }

    fun loadPersistedQueue(
        onQueueLoaded: (ListQueue) -> Unit
    ) {
        if (dataStore.get(PersistentQueueKey, true)) {
            runCatching {
                context.filesDir.resolve(PERSISTENT_QUEUE_FILE)
                    .inputStream()
                    .use { fis ->
                        ObjectInputStream(fis).use { oos ->
                            oos.readObject() as PersistQueue
                        }
                    }
            }.onSuccess { queue ->
                onQueueLoaded(
                    ListQueue(
                        title = queue.title,
                        items = queue.items,
                        startIndex = queue.mediaItemIndex,
                        position = queue.position,
                    )
                )
            }
        }
    }

    fun saveQueueToDisk(
        player: ExoPlayer,
        queueTitle: String?,
        automixItems: MutableStateFlow<List<MediaItem>>
    ) {
        if (!dataStore.get(PersistentQueueKey, true)) return

        if (player.playbackState == STATE_IDLE) {
            context.filesDir.resolve(PERSISTENT_AUTOMIX_FILE).delete()
            context.filesDir.resolve(PERSISTENT_QUEUE_FILE).delete()
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
            context.filesDir.resolve(PERSISTENT_QUEUE_FILE)
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
            context.filesDir.resolve(PERSISTENT_AUTOMIX_FILE)
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
}