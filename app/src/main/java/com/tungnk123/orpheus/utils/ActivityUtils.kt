package com.tungnk123.orpheus.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object ActivityUtils {
    fun startBrowserActivity(activity: Context, uri: Uri) {
        activity.startActivity(Intent(Intent.ACTION_VIEW).setData(uri))
    }

    fun makePersistableReadableUri(context: Context, uri: Uri) {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    fun releasePersistableReadableUri(context: Context, uri: Uri) {
        context.contentResolver.releasePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
}
