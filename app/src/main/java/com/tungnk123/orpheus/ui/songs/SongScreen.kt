package com.tungnk123.orpheus.ui.songs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tungnk123.orpheus.ui.common.CenterAlignedTopBarWithSearch
import com.tungnk123.orpheus.ui.common.SongCard
import com.tungnk123.orpheus.ui.songs.composables.SongDropdownMenu

@Composable
fun SongScreen(
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel = hiltViewModel()
) {
    val songs by songViewModel.songs.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopBarWithSearch(
                title = "Songs",
                onSearchClick = {}
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
                            onShareSongClick = {},
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