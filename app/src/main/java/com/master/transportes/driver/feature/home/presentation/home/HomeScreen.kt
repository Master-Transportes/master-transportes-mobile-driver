package com.master.transportes.driver.feature.home.presentation.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.master.transportes.driver.feature.home.presentation.home.permission.rememberLocationPermissionHandler

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.actionErrorMessage.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    val permissionHandler = rememberLocationPermissionHandler(
        onPermissionResult = viewModel::onLocationPermissionResult
    )

    HomeContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onGoOnline = viewModel::onGoOnline,
        onGoOffline = viewModel::onGoOffline,
        onRetryLoadDriver = viewModel::retryLoadDriver,
        onRetryLoadStatus = viewModel::retryLoadStatus,
        onOpenLocationSettings = permissionHandler.onOpenLocationSettings,
        onOpenAppPermissionSettings = permissionHandler.onOpenAppPermissionSettings
    )
}