package com.tungnk123.orpheus.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Utility class for handling Activity and URI-related operations.
 */

object ActivityUtils {
    fun startBrowserActivity(activity: Context, uri: Uri) {
        activity.startActivity(Intent(Intent.ACTION_VIEW).setData(uri))
    }

    fun makePersistableReadableUri(context: Context, uri: Uri): Boolean  {
        val hasPermission = context.contentResolver.persistedUriPermissions
                    .any { it.uri == uri && it.isReadPermission }
        if (hasPermission) {
            return true
        }

        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            return true
        }
        catch (e: SecurityException) {
            Log.e("ActivityUtils", "Failed to take URI permission", e)
            return false
        }
    }

    fun releasePersistableReadableUri(context: Context, uri: Uri) {
        try {
            context.contentResolver.releasePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
