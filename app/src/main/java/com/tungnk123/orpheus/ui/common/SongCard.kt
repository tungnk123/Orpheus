package com.tungnk123.orpheus.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tungnk123.orpheus.data.model.Song
import com.tungnk123.orpheus.helper.AssetsHelper

enum class SongCardThumbnailLabelStyle {
    Default,
    Subtle,
}

private fun SongCardThumbnailLabelStyle.backgroundColor(colorScheme: ColorScheme) = when (this) {
    SongCardThumbnailLabelStyle.Default -> colorScheme.surfaceVariant
    SongCardThumbnailLabelStyle.Subtle -> colorScheme.surfaceVariant
}

private fun SongCardThumbnailLabelStyle.contentColor(colorScheme: ColorScheme) = when (this) {
    SongCardThumbnailLabelStyle.Default -> colorScheme.primary
    SongCardThumbnailLabelStyle.Subtle -> colorScheme.onSurfaceVariant
}

@Composable
fun SongCard(
    song: Song,
    highlighted: Boolean = false,
    autoHighlight: Boolean = true,
    disableHeartIcon: Boolean = false,
    leading: @Composable () -> Unit = {},
    thumbnailLabel: (@Composable () -> Unit)? = null,
    thumbnailLabelStyle: SongCardThumbnailLabelStyle = SongCardThumbnailLabelStyle.Default,
    trailingOptionsContent: (@Composable ColumnScope.(() -> Unit) -> Unit)? = null,
    onClick: () -> Unit,
) {
//    val queue by context.symphony.radio.observatory.queue.collectAsState()
//    val queueIndex by context.symphony.radio.observatory.queueIndex.collectAsState()
//    val isCurrentPlaying by remember(autoHighlight, song, queue) {
//        derivedStateOf { autoHighlight && song.id == queue.getOrNull(queueIndex) }
//    }
//    val favoriteSongIds by context.symphony.groove.playlist.favorites.collectAsState()
//    val isFavorite by remember(favoriteSongIds, song) {
//        derivedStateOf { favoriteSongIds.contains(song.id) }
//    }
    val isCurrentPlaying by remember {
        mutableStateOf(false)
    }
    val isFavorite by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.padding(
                12.dp,
                12.dp,
                4.dp,
                12.dp
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                leading()
                Box {
                    AsyncImage(
                        AssetsHelper.placeholderDarkId,
                        null,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                    thumbnailLabel?.let { it ->
                        val backgroundColor =
                            thumbnailLabelStyle.backgroundColor(MaterialTheme.colorScheme)
                        val contentColor =
                            thumbnailLabelStyle.contentColor(MaterialTheme.colorScheme)

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp)
                                .align(Alignment.BottomCenter)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        backgroundColor,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(3.dp, 0.dp)

                            ) {
                                ProvideTextStyle(
                                    MaterialTheme.typography.labelSmall.copy(
                                        color = contentColor
                                    )
                                ) { it() }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        song.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = when {
                                highlighted || isCurrentPlaying -> MaterialTheme.colorScheme.primary
                                else -> LocalTextStyle.current.color
                            }
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (song.artists.isNotEmpty()) {
                        Text(
                            song.artists.joinToString(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(15.dp))

                Row {
                    if (!disableHeartIcon && isFavorite) {
                        IconButton(
                            modifier = Modifier.offset(
                                4.dp,
                                0.dp
                            ),
                            onClick = {
//                                context.symphony.groove.playlist.unfavorite(song.id)
                            }
                        ) {
                            Icon(
                                Icons.Filled.Favorite,
                                null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    var showOptionsMenu by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = { showOptionsMenu = !showOptionsMenu }
                    ) {
                        Icon(
                            Icons.Filled.MoreVert,
                            null,
                            modifier = Modifier.size(24.dp),
                        )
//                        SongDropdownMenu(
//                            song = song,
//                            isFavorite = false,
//                            trailingContent = trailingOptionsContent,
//                            expanded = showOptionsMenu,
//                            onDismissRequest = {
//                                showOptionsMenu = false
//                            }
//                        )
                    }
                }
            }
        }
    }
}
