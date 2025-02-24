package com.tungnk123.orpheus.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GradientOverlay(
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .width(12.dp)
                .fillMaxHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            backgroundColor,
                            Color.Transparent
                        )
                    )
                )
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .width(12.dp)
                .fillMaxHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            backgroundColor
                        )
                    )
                )
        )
    }
}