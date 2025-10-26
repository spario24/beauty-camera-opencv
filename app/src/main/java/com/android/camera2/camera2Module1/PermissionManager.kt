package com.android.camera2.camera2Module1

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    /**
     * List of required permissions depending on Android version.
     */
    fun getRequiredPermissions(): Array<String> {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        // Storage permissions differ by Android version
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                // WRITE_EXTERNAL_STORAGE required for Android 9 (API 28) and below
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // For Android 13+, use new scoped permissions if needed
                permissions.addAll(
                    listOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO
                    )
                )
            }
            else -> {
                // For Android 10–12, only READ_EXTERNAL_STORAGE might be needed
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        return permissions.toTypedArray()
    }

    /**
     * Check if a specific permission is granted.
     */
    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if all required permissions are granted.
     */
    fun hasAllPermissions(): Boolean {
        return getRequiredPermissions().all { isPermissionGranted(it) }
    }

    fun hasCameraPermission() = isPermissionGranted(Manifest.permission.CAMERA)

    fun hasAudioPermission() = isPermissionGranted(Manifest.permission.RECORD_AUDIO)

    fun hasStoragePermission(): Boolean {
        return when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ->
                isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES) &&
                        isPermissionGranted(Manifest.permission.READ_MEDIA_VIDEO)
            }
            else ->
                isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    /**
     * Request a specific permission or all permissions.
     */
    fun requestPermissions(activity: Activity, permissions: Array<String>? = null) {
        val toRequest = permissions ?: getRequiredPermissions()
        ActivityCompat.requestPermissions(activity, toRequest, PERMISSION_REQUEST_CODE)
    }

    /**
     * Handle results from onRequestPermissionsResult.
     */
    fun arePermissionsGranted(grantResults: IntArray): Boolean {
        return grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    }
}
