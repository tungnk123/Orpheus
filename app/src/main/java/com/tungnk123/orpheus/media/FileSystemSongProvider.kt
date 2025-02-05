package com.tungnk123.orpheus.media

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.tungnk123.orpheus.data.model.Song
import com.tungnk123.orpheus.helper.MediaMetadataHelper
import com.tungnk123.orpheus.utils.extensions.printLog
import com.tungnk123.orpheus.utils.file.DocumentFileX
import com.tungnk123.orpheus.utils.file.SimpleFileSystem
import com.tungnk123.orpheus.utils.file.SimplePath
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileSystemSongProvider @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        const val TAG = "FileSystemSongProvider"
        const val MIMETYPE_M3U = "audio/x-mpegurl"
    }

    private val uris = ConcurrentHashMap<String, Uri>()
    private var explorer = SimpleFileSystem.Folder()
    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    private fun emitUpdate(value: Boolean) = _isUpdating.update { value }

    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun fetch() {
        emitUpdate(true)
        try {
            val folderUris = getMediaFolders()
            Log.d(TAG, "Fetching songs from folders: $folderUris")

            folderUris.map { uri ->
                DocumentFileX.fromTreeUri(context, uri)?.let { rootFolder ->
                    val path = SimplePath(DocumentFileX.getParentPathOfTreeUri(uri) ?: rootFolder.name)
                    Log.d(TAG, "Scanning folder: $path")
                    withContext(Dispatchers.IO) {
                        scanMediaTree(path, rootFolder)
                    }
                }
            }
        } catch (ioErr: IOException) {
            Log.e(TAG, "Error during fetch()", ioErr)
        }
        emitUpdate(false)
    }

    private fun getMediaFolders(): List<Uri> {
        val folders = listOf(Uri.parse("file:///storage/emulated/0/Music"))
        Log.d(TAG, "Media folders: $folders")
        return folders
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private suspend fun scanMediaTree(path: SimplePath, file: DocumentFileX) {
        try {
            Log.d(TAG, "Scanning media tree: $path")
            coroutineScope {
                val jobs = file.list().map { childFile ->
                    async(Dispatchers.IO) {
                        val childPath = path.join(childFile.name)
                        when {
                            childFile.isDirectory -> {
                                Log.d(TAG, "Found folder: $childPath - Scanning recursively...")
                                scanMediaTree(childPath, childFile)
                            }
                            else -> {
                                Log.d(TAG, "Found file: $childPath - Processing...")
                                scanMediaFile(childPath, childFile)
                            }
                        }
                    }
                }
                jobs.awaitAll()
            }
        } catch (err: Exception) {
            Log.e(TAG, "Error scanning media tree at $path", err)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private suspend fun scanMediaFile(path: SimplePath, file: DocumentFileX) {
        try {
            when {
                path.extension == "lrc" -> {
                    Log.d(TAG, "Processing LRC file: $path")
                    scanLrcFile(path, file)
                }
                file.mimeType == MIMETYPE_M3U -> {
                    Log.d(TAG, "Processing M3U file: $path")
                    scanM3UFile(path, file)
                }
                file.mimeType.startsWith("audio/") -> {
                    Log.d(TAG, "Processing Audio file: $path")
                    scanAudioFile(path, file)
                }
                else -> Log.d(TAG, "Skipping non-audio file: $path")
            }
        } catch (err: Exception) {
            Log.e(TAG, "Error processing file: $path", err)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private suspend fun scanAudioFile(path: SimplePath, file: DocumentFileX) {
        val pathString = path.pathString
        uris[pathString] = file.uri

        Log.d(TAG, "Extracting metadata for: $pathString")

        val metadata = withContext(Dispatchers.IO) {
            MediaMetadataHelper.getMetadataFromFile(file.uri.toString())
        }
        Log.d(TAG, "Metadata extracted: $metadata")

        val song = Song(
            id = file.uri.toString(),
            title = metadata.title ?: path.nameWithoutExtension,
            album = metadata.album,
            artists = metadata.artist?.split(";")?.toSet() ?: emptySet(),
            composers = emptySet(),
            albumArtists = emptySet(),
            genres = emptySet(),
            date = metadata.date,
            year = metadata.year ?: metadata.date?.year,
            duration = metadata.duration ?: 0L,
            bitrate = metadata.bitrate,
            samplingRate = metadata.samplingRate,
            channels = metadata.channels,
            encoder = metadata.encoder,
            discNumber = null,
            discTotal = null,
            coverFile = metadata.coverFile,
            dateModified = file.lastModified,
            size = file.size,
            uri = file.uri,
            path = pathString
        )

        Log.d(TAG, "Song added: ${song.title} - ${song.artists}")

        explorer.addChildFile(path)

        withContext(Dispatchers.Main) {
            emitSong(song)
        }
    }

    private fun scanLrcFile(path: SimplePath, file: DocumentFileX) {
        uris[path.pathString] = file.uri
        explorer.addChildFile(path)
        Log.d(TAG, "LRC file added: $path")
    }

    private fun scanM3UFile(path: SimplePath, file: DocumentFileX) {
        uris[path.pathString] = file.uri
        explorer.addChildFile(path)
        Log.d(TAG, "M3U playlist file added: $path")
    }

    suspend fun reset() {
        emitUpdate(true)
        Log.d(TAG, "Resetting FileSystemSongProvider...")
        uris.clear()
        explorer = SimpleFileSystem.Folder()
        emitUpdate(false)
    }

    private fun emitSong(song: Song) {
        Log.d(TAG, "Emitting song: ${song.title} - ${song.artists}")
    }
}
