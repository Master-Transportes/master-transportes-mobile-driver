package com.master.transportes.driver.feature.home.presentation.home.permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect

class LocationPermissionHandler(
    val onOpenLocationSettings: () -> Unit,
    val onOpenAppPermissionSettings: () -> Unit
)

@Composable
fun rememberLocationPermissionHandler(
    onPermissionResult: (Boolean) -> Unit
): LocationPermissionHandler {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        onPermissionResult(granted)
    }

    var hasRequestedPermission by remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            onPermissionResult(true)
        } else if (!hasRequestedPermission) {
            hasRequestedPermission = true
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        onPauseOrDispose { }
    }

    val activity = context as? Activity
    val shouldShowRationale = activity?.shouldShowRequestPermissionRationale(
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == true

    val onOpenLocationSettings = {
        context.startActivity(
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        )
    }

    val onOpenAppPermissionSettings = {
        if (shouldShowRationale) {
            permissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    "package:${context.packageName}".toUri()
                )
            )
        }
    }

    return LocationPermissionHandler(
        onOpenLocationSettings = onOpenLocationSettings,
        onOpenAppPermissionSettings = onOpenAppPermissionSettings
    )
}
