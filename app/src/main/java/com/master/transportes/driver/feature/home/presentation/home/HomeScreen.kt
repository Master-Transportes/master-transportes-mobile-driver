package com.master.transportes.driver.feature.home.presentation.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onLocationPermissionResult(granted)
    }

    var hasRequestedPermission by remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.onLocationPermissionResult(true)
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

    HomeContent(
        state = state,
        onOpenLocationSettings = {
            context.startActivity(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            )
        },
        onOpenAppPermissionSettings = {
            if (shouldShowRationale) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}")
                    )
                )
            }
        }
    )
}
