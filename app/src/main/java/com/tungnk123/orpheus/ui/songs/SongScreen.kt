package com.tungnk123.orpheus.ui.songs

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tungnk123.orpheus.R
import com.tungnk123.orpheus.data.model.Song
import com.tungnk123.orpheus.playback.LocalPlayerConnection
import com.tungnk123.orpheus.ui.common.AnimatedNowPlayingBottomBar
import com.tungnk123.orpheus.ui.common.CenterAlignedTopBarWithSearch
import com.tungnk123.orpheus.ui.common.SongCard
import com.tungnk123.orpheus.ui.songs.composables.SongDropdownMenu
import com.tungnk123.orpheus.utils.extensions.showToast
import com.tungnk123.orpheus.utils.extensions.togglePlayPause

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun SongScreen(
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel = hiltViewModel(),
) {
    val songs by songViewModel.songs.collectAsStateWithLifecycle()
    val playerConnection = LocalPlayerConnection.current
    val context = LocalContext.current
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopBarWithSearch(
                title = stringResource(R.string.songs),
                onSearchClick = {},
                onRescanClick = {
                    songViewModel.fetchSongsFromMediaStoreAndSaveToDatabase()
                },
                onAdvancedRescanClick = {},
                onSettingsClick = {}
            )
        },
        bottomBar = {
            val mockSong = Song(
                id = "1",
                title = "Sample Song",
                album = "Sample Album",
            )
            AnimatedNowPlayingBottomBar(
                insetPadding = true,
                currentPlayingSong = mockSong,
                isPlaying = true,
                playbackPosition = 0.5f,
                showTrackControls = true,
                showSeekControls = true,
                seekBackDuration = 500,
                seekForwardDuration = 500,
                onPlayPause = {
                    playerConnection?.player?.togglePlayPause()
                    context.showToast("onPlayPause Click")
                },
                onSkipNext = {
                    context.showToast("onSkipNext Click")
                    true
                },
                onSkipPrevious = {
                    context.showToast("onSkipPrevious Click")
                    true
                },
                onSeekForward = {
                    context.showToast("onSeekForward Click")
                },
                onSeekBackward = {
                    context.showToast("onSeekBackward Click")
                },
                onStop = {
                    context.showToast("onStop Click")
                },
                onExpandPlayer = {
                    context.showToast("onExpandPlayer Click")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPaddings ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            items(songs) { item ->
                var expanded by remember {
                    mutableStateOf(false)
                }
                SongCard(
                    song = item,
                    onClick = {},
                    showOptionsMenu = expanded,
                    onMenuClick = {
                        expanded = !expanded
                    },
                    menuContent = {
                        SongDropdownMenu(
                            song = item,
                            isFavorite = item.isFavorite,
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            },
                            onShareSongClick = {
                                context.showToast("Share song Click")
                            },
                            onFavoriteChange = {},
                            onViewAlbumClick = {},
                            onViewArtistClick = {},
                            onAddToQueueClick = {},
                            onPlayNextClick = {},
                            onViewAlbumArtistClick = {},
                        )
                    }
                )
            }
        }
    }
}