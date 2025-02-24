package com.tungnk123.orpheus.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.tungnk123.orpheus.data.model.Song

@Composable
fun NowPlayingBottomBarContent(
    song: Song,
    onSkip: () -> Boolean,
    onPrevious: () -> Boolean,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.clipToBounds()) {
        val cardWidthPx = this@BoxWithConstraints.constraints.maxWidth
        var offsetX by remember { mutableFloatStateOf(0f) }
        val cardOffsetX by animateFloatAsState(
            offsetX / 2,
            label = "c-now-playing-card-offset-x",
        )
        val cardOpacity by animateFloatAsState(
            if (offsetX != 0f) 0.7f else 1f,
            label = "c-now-playing-card-opacity",
        )

        Box(
            modifier = Modifier
                .graphicsLayer(
                    alpha = cardOpacity,
                    translationX = cardOffsetX
                )
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val thresh = cardWidthPx / 4
                            val affected = when {
                                -offsetX > thresh -> onSkip()
                                offsetX > thresh -> onPrevious()
                                else -> false
                            }
                            if (!affected) {
                                offsetX = 0f
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX += dragAmount
                        },
                    )
                },
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                NowPlayingBottomBarContentText(
                    song.title,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (song.artists.isNotEmpty()) {
                    NowPlayingBottomBarContentText(
                        song.artists.joinToString(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}