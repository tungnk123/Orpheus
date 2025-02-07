package com.tungnk123.orpheus.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.ui.graphics.vector.ImageVector
import com.tungnk123.orpheus.R

enum class NavigationBarItem(
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    ForYou(
        labelResId = R.string.for_you,
        selectedIcon = Icons.Filled.Face,
        unselectedIcon = Icons.Outlined.Face
    ),
    Songs(
        labelResId = R.string.songs,
        selectedIcon = Icons.Filled.MusicNote,
        unselectedIcon = Icons.Outlined.MusicNote
    ),
    Artists(
        labelResId = R.string.artists,
        selectedIcon = Icons.Filled.Group,
        unselectedIcon = Icons.Outlined.Group
    ),
    Albums(
        labelResId = R.string.albums,
        selectedIcon = Icons.Filled.Album,
        unselectedIcon = Icons.Outlined.Album
    ),
    AlbumArtists(
        labelResId = R.string.album_artists,
        selectedIcon = Icons.Filled.SupervisorAccount,
        unselectedIcon = Icons.Outlined.SupervisorAccount
    ),
    Genres(
        labelResId = R.string.genres,
        selectedIcon = Icons.Filled.Tune,
        unselectedIcon = Icons.Outlined.Tune
    ),
    Playlists(
        labelResId = R.string.playlists,
        selectedIcon = Icons.Filled.AccountTree,
        unselectedIcon = Icons.Outlined.AccountTree
    ),
    Browser(
        labelResId = R.string.browser,
        selectedIcon = Icons.Filled.Folder,
        unselectedIcon = Icons.Outlined.Folder
    ),
    Folders(
        labelResId = R.string.folders,
        selectedIcon = Icons.Filled.FolderOpen,
        unselectedIcon = Icons.Outlined.FolderOpen
    )
}
