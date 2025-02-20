package com.tungnk123.orpheus.helper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import com.tungnk123.orpheus.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionHandler @Inject constructor() {
    data class PermissionState(
        val requiredPermissions: List<String>,
        val grantedPermissions: List<String>,
        val deniedPermissions: List<String>,
    ) {
        fun hasAllPermissions() = deniedPermissions.isEmpty()
    }

    fun handle(activity: MainActivity) {
        val permissionState = getPermissionState(activity)
        if (permissionState.hasAllPermissions()) {
            return
        }
        val contract = ActivityResultContracts.RequestMultiplePermissions()
        activity.registerForActivityResult(contract) {}
            .launch(permissionState.deniedPermissions.toTypedArray())
    }

    private fun getRequiredPermissions(): List<String> {
        val required = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            required.add(Manifest.permission.POST_NOTIFICATIONS)
            required.add(Manifest.permission.READ_MEDIA_AUDIO)
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            required.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        return required
    }


    private fun getPermissionState(activity: MainActivity): PermissionState {
        val requiredPermissions = getRequiredPermissions()
        val grantedPermissions = mutableListOf<String>()
        val deniedPermissions = mutableListOf<String>()
        requiredPermissions.forEach {
            if (activity.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(it)
            } else {
                deniedPermissions.add(it)
            }
        }
        return PermissionState(
            requiredPermissions = requiredPermissions,
            grantedPermissions = grantedPermissions,
            deniedPermissions = deniedPermissions
        )
    }
}
