package com.tungnk123.orpheus.ui.songs.composables

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tungnk123.orpheus.R
import com.tungnk123.orpheus.data.model.Song

@Composable
fun SongDropdownMenu(
    song: Song,
    isFavorite: Boolean,
    expanded: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    onPlayNextClick: () -> Unit,
    onAddToQueueClick: () -> Unit,
    onViewArtistClick: (String) -> Unit,
    onViewAlbumArtistClick: (String) -> Unit,
    onViewAlbumClick: (String) -> Unit,
    onShareSongClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable ColumnScope.(() -> Unit) -> Unit)? = null,
) {
    var showInfoDialog by remember { mutableStateOf(false) }
    var showAddToPlaylistDialog by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null
                )
            },
            text = {
                Text(
                    stringResource(if (isFavorite) R.string.remove_favorite else R.string.favorite)
                )
            },
            onClick = {
                onDismissRequest()
                onFavoriteChange(!isFavorite)
            })
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
                    contentDescription = null
                )
            },
            text = {
                Text(stringResource(R.string.play_next))
            },
            onClick = {
                onDismissRequest()
                onPlayNextClick()
            })
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.PlaylistPlay,
                    null
                )
            },
            text = {
                Text(stringResource(R.string.add_to_queue))
            },
            onClick = {
                onDismissRequest()
                onAddToQueueClick()
            })
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.PlaylistAdd,
                    null
                )
            },
            text = {
                Text(stringResource(R.string.add_to_playlist))
            },
            onClick = {
                onDismissRequest()
                showAddToPlaylistDialog = true
            })
        song.artists.forEach { artistName ->
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        Icons.Filled.Person,
                        null
                    )
                },
                text = {
                    Text(
                        stringResource(
                            R.string.view_artist,
                            artistName
                        )
                    )
                },
                onClick = {
                    onDismissRequest()
                    onViewArtistClick(artistName)
                })
        }
        song.albumArtists.forEach { albumArtist ->
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        Icons.Filled.Person,
                        null
                    )
                },
                text = {
                    Text(
                        stringResource(
                            R.string.view_album_artist,
                            albumArtist
                        )
                    )
                },
                onClick = {
                    onDismissRequest()
                    onViewAlbumArtistClick(albumArtist)
                })
        }
        song.album?.let { albumId ->
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        Icons.Filled.Album,
                        null
                    )
                },
                text = {
                    Text(
                        stringResource(
                            R.string.view_album,
                            albumId
                        ))
                },
                onClick = {
                    onDismissRequest()
                    onViewAlbumClick(albumId)
                })
        }
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    Icons.Filled.Share,
                    null
                )
            },
            text = {
                Text(stringResource(R.string.share_song))
            },
            onClick = {
                onDismissRequest()
                onShareSongClick()
            })
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    Icons.Filled.Info,
                    null
                )
            },
            text = {
                Text(stringResource(R.string.view_details))
            },
            onClick = {
                onDismissRequest()
                showInfoDialog = true
            })
        trailingContent?.invoke(
            this,
            onDismissRequest
        )
    }

    if (showInfoDialog) {
//        SongInformationDialog(
//            context,
//            song = song,
//            onDismissRequest = {
//                showInfoDialog = false
//            })
    }

    if (showAddToPlaylistDialog) {
//        AddToPlaylistDialog(
//            context,
//            songIds = listOf(song.id),
//            onDismissRequest = {
//                showAddToPlaylistDialog = false
//            })
    }
}