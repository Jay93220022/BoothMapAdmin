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
                    Toast.makeText(
                        context,
                        "Some permissions were denied. Features may be limited.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        LaunchedEffect(Unit) {
            val permissionsToRequest = mutableListOf(
                Manifest.permission.CAMERA
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