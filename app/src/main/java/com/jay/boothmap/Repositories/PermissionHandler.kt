package com.jay.boothmap.Repositories

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat

class PermissionHandler {
    fun hasStoragePermissions(context: Context): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasLocationPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    @Composable
    fun RequestPermissions(
        context: Context,
        onPermissionsGranted: () -> Unit = {}
    ) {
        val requestPermissions =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val allGranted = permissions.entries.all { it.value }
                if (allGranted) {
                    onPermissionsGranted()
                } else {
                    // Check specifically which permissions were denied
                    val locationDenied = !permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                            !permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
                    val cameraDenied = !permissions.getOrDefault(Manifest.permission.CAMERA, false)
                    val storageDenied = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        !permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false)
                    } else {
                        !permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false)
                    }

                    val message = when {
                        locationDenied && (cameraDenied || storageDenied) ->
                            "Location and media permissions are required for full functionality"
                        locationDenied -> "Location permission is required for mapping features"
                        cameraDenied || storageDenied -> "Camera/Storage permissions are required for media features"
                        else -> "Some permissions were denied. Features may be limited."
                    }

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

        LaunchedEffect(Unit) {
            val permissionsToRequest = mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).apply {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

            requestPermissions.launch(permissionsToRequest.toTypedArray())
        }
    }
}