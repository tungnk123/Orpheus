package com.tungnk123.orpheus.utils.uihelper

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.ui.unit.Dp
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.fallback
import coil3.request.placeholder

enum class ScreenOrientation {
    PORTRAIT,
    LANDSCAPE;

    val isPortrait: Boolean get() = this == PORTRAIT
    val isLandscape: Boolean get() = this == LANDSCAPE

    companion object {
        fun fromConfiguration(configuration: Configuration) = when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> LANDSCAPE
            else -> PORTRAIT
        }

        fun fromConstraints(constraints: BoxWithConstraintsScope) =
            fromDimension(constraints.maxHeight, constraints.maxWidth)

        fun fromDimension(height: Dp, width: Dp) = when {
            width.value > height.value -> LANDSCAPE
            else -> PORTRAIT
        }
    }
}

fun createHandyImageRequest(context: Context, image: Any, fallback: Int) =
    createHandyImageRequest(context, image, fallbackResId = fallback)

private fun createHandyImageRequest(
    context: Context,
    image: Any,
    fallbackResId: Int? = null,
) = ImageRequest.Builder(context).apply {
    data(image)
    fallbackResId?.let {
        placeholder(it)
        fallback(it)
        error(it)
    }
    crossfade(true)
}
