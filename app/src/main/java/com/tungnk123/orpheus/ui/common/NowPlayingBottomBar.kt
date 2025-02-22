package com.tungnk123.orpheus.ui.common

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tungnk123.orpheus.R
import com.tungnk123.orpheus.data.model.Song
import com.tungnk123.orpheus.utils.extensions.swipeable
import com.tungnk123.orpheus.utils.uihelper.TransitionDurations

@Composable
fun AnimatedNowPlayingBottomBar(
    insetPadding: Boolean,
    currentPlayingSong: Song?,
    isPlaying: Boolean,
    playbackPosition: Float,
    showTrackControls: Boolean,
    showSeekControls: Boolean,
    seekBackDuration: Int,
    seekForwardDuration: Int,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Boolean,
    onSkipPrevious: () -> Boolean,
    onSeekForward: (Int) -> Unit,
    onSeekBackward: (Int) -> Unit,
    onStop: () -> Unit,
    onExpandPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visible = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visible,
        enter = slideInVertically(
            animationSpec = nowPlayingBottomBarEnterAnimationSpec(),
            initialOffsetY = { it / 2 },
        ) + fadeIn(animationSpec = nowPlayingBottomBarEnterAnimationSpec()),
        exit = fadeOut(),
        modifier = modifier
    ) {
        NowPlayingBottomBar(
            insetPadding = insetPadding,
            currentPlayingSong = currentPlayingSong,
            isPlaying = isPlaying,
            playbackPosition = playbackPosition,
            showTrackControls = showTrackControls,
            showSeekControls = showSeekControls,
            seekBackDuration = seekBackDuration,
            seekForwardDuration = seekForwardDuration,
            onPlayPause = onPlayPause,
            onSkipNext = onSkipNext,
            onSkipPrevious = onSkipPrevious,
            onSeekForward = onSeekForward,
            onSeekBackward = onSeekBackward,
            onStop = onStop,
            onExpandPlayer = onExpandPlayer
        )
    }
}

private fun <T> nowPlayingBottomBarEnterAnimationSpec() = TransitionDurations.Normal.asTween<T>(
    delayMillis = TransitionDurations.Fast.milliseconds,
)

@Composable
fun NowPlayingBottomBar(
    insetPadding: Boolean,
    currentPlayingSong: Song?,
    isPlaying: Boolean,
    playbackPosition: Float,
    showTrackControls: Boolean,
    showSeekControls: Boolean,
    seekBackDuration: Int,
    seekForwardDuration: Int,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Boolean,
    onSkipPrevious: () -> Boolean,
    onSeekForward: (Int) -> Unit,
    onSeekBackward: (Int) -> Unit,
    onStop: () -> Unit,
    onExpandPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AnimatedContent(modifier = modifier.fillMaxWidth(),
        label = "c-now-playing-container",
        targetState = currentPlayingSong,
        contentKey = { it != null },
        transitionSpec = {
            val from = slideInVertically() + fadeIn()
            val to = slideOutVertically() + fadeOut()
            from togetherWith to
        }) { currentPlayingSongTarget ->
        currentPlayingSongTarget?.let { currentSong ->
            Column {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.25f))
                        .height(2.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .background(MaterialTheme.colorScheme.surfaceTint)
                            .fillMaxHeight()
                    )
                }
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .swipeable(
                            onSwipeUp = onExpandPlayer,
                            onSwipeDown = onStop,
                        ),
                    shape = RectangleShape,
                    onClick = onExpandPlayer
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            0.dp,
                            8.dp
                        ),
                    ) {
                        Spacer(modifier = Modifier.width(12.dp))
                        AnimatedContent(
                            label = "c-now-playing-card-image",
                            targetState = currentSong,
                            contentKey = { it.id },
                            transitionSpec = {
                                val from = fadeIn(
                                    animationSpec = TransitionDurations.Normal.asTween(
                                        delayMillis = TransitionDurations.Normal.milliseconds,
                                    ),
                                )
                                val to = fadeOut(TransitionDurations.Normal.asTween())
                                from togetherWith to
                            },
                        ) { song ->
                            AsyncImage(
                                model = song.createArtworkImageRequest(context),
                                contentDescription = stringResource(R.string.msg_album_artwork),
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                onError = {
                                    Log.e(
                                        "ImageDebug",
                                        "Image Load Failed: ${it.result.throwable}"
                                    )
                                },
                                onSuccess = {
                                    Log.d(
                                        "ImageDebug",
                                        "Image Loaded Successfully"
                                    )
                                }
                            )
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        AnimatedContent(
                            label = "c-now-playing-card-content",
                            modifier = Modifier.weight(1f),
                            targetState = currentSong,
                            contentKey = { it.id },
                            transitionSpec = {
                                val from = fadeIn(
                                    animationSpec = TransitionDurations.Normal.asTween(
                                        delayMillis = TransitionDurations.Normal.milliseconds,
                                    ),
                                )
                                val to = fadeOut(TransitionDurations.Normal.asTween())
                                from togetherWith to
                            },
                        ) { song ->
                            NowPlayingBottomBarContent(
                                song = song,
                                onSkip = onSkipNext,
                                onPrevious = onSkipPrevious
                            )
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        if (showTrackControls) {
                            IconButton(onClick = {
                                onSkipPrevious()
                            }) {
                                Icon(
                                    Icons.Filled.SkipPrevious,
                                    null
                                )
                            }
                        }
                        if (showSeekControls) {
                            IconButton(
                                onClick = { onSeekBackward(seekBackDuration) }
                            ) {
                                Icon(
                                    Icons.Filled.FastRewind,
                                    null
                                )
                            }
                        }
                        IconButton(
                            onClick = onPlayPause
                        ) {
                            Icon(
                                when {
                                    !isPlaying -> Icons.Filled.PlayArrow
                                    else -> Icons.Filled.Pause
                                },
                                null
                            )
                        }
                        if (showSeekControls) {
                            IconButton(
                                onClick = { onSeekForward(seekForwardDuration) }
                            ) {
                                Icon(
                                    Icons.Filled.FastForward,
                                    null
                                )
                            }
                        }
                        if (showTrackControls) {
                            IconButton(onClick = {
                                onSkipNext()
                            }) {
                                Icon(
                                    Icons.Filled.SkipNext,
                                    null
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                if (insetPadding) {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        } ?: Box {}
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNowPlayingBottomBar() {
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
        showTrackControls = false,
        showSeekControls = false,
        seekBackDuration = 500,
        seekForwardDuration = 500,
        onPlayPause = {},
        onSkipNext = { true },
        onSkipPrevious = { true },
        onSeekForward = {},
        onSeekBackward = {},
        onStop = {},
        onExpandPlayer = {},
        modifier = Modifier.fillMaxWidth()
    )
}