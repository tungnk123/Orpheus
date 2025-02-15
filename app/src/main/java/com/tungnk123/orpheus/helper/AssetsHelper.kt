package com.tungnk123.orpheus.helper

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.fallback
import coil3.request.placeholder
import com.tungnk123.orpheus.R

object AssetsHelper {
    private val placeholderDarkId = R.raw.placeholder_dark
    private val placeholderLightId = R.raw.placeholder_light

    private fun getPlaceholderId(isLight: Boolean = false) = when {
        isLight -> placeholderLightId
        else -> placeholderDarkId
    }

    fun getPlaceholderUri(context: Context) = buildUriOfResource(
        resources = context.resources,
        resourceId = getPlaceholderId(),
    )

    fun createPlaceholderImageRequest(context: Context) =
        ImageRequest.Builder(context)
            .data(getPlaceholderUri(context))

    private fun buildUriOfResource(
        resources: Resources,
        resourceId: Int
    ): Uri {
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(resourceId))
            .appendPath(resources.getResourceTypeName(resourceId))
            .appendPath(resources.getResourceEntryName(resourceId))
            .build()
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

}
