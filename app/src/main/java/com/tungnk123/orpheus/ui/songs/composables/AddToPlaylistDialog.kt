package com.tungnk123.orpheus.ui.songs.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tungnk123.orpheus.data.model.Playlist
import com.tungnk123.orpheus.ui.common.ScaffoldDialog
import com.tungnk123.orpheus.ui.common.SongCard
import com.tungnk123.orpheus.ui.common.SubtleCaptionText

@Composable
fun AddToPlaylistDialog(
    songIds: List<String>,
    playlists: List<Playlist>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showNewPlaylistDialog by remember { mutableStateOf(false) }

    ScaffoldDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = {
            Text("Add to playlist")
        },
        content = {
            when {
                playlists.isEmpty() -> SubtleCaptionText("No in-app playlist found!")
                else -> LazyColumn(modifier = Modifier.padding(bottom = 4.dp)) {
                    items(playlists) { playlist ->
                        val playlistSongPaths = playlist.songPaths

                        SongCard(
                            image = null,
                            imageLabel = when {
                                songIds.size == 1 && playlistSongPaths.contains(songIds[0]) -> ({
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                    )
                                })

                                else -> null
                            },
                            title = {
                                Text(playlist.title)
                            },
                            options = { expanded, onDismissRequest ->
//                                PlaylistDropdownMenu(
//                                    context,
//                                    playlist,
//                                    expanded = expanded,
//                                    onDismissRequest = onDismissRequest,
//                                )
                            },
                            onClick = {
//                                context.symphony.groove.playlist.update(
//                                    playlist.id,
//                                    playlistSongIds.mutate { addAll(songIds) },
//                                )
                                onDismissRequest()
                            }
                        )
                    }
                }
            }
        },
        removeActionsVerticalPadding = true,
        actions = {
            TextButton(
                modifier = Modifier.offset(y = (-8).dp),
                onClick = {
                    showNewPlaylistDialog = !showNewPlaylistDialog
                }
            ) {
                Text("Create new playlist")
            }
            Spacer(modifier = Modifier.weight(1f))
        },
    )

    if (showNewPlaylistDialog) {
//        NewPlaylistDialog(
//            context = context,
//            onDone = { playlist ->
//                showNewPlaylistDialog = false
//                context.symphony.groove.playlist.add(playlist)
//            },
//            onDismissRequest = {
//                showNewPlaylistDialog = false
//            }
//        )
    }
}
