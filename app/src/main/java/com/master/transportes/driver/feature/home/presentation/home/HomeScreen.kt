package com.master.transportes.driver.feature.home.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.master.transportes.driver.feature.home.presentation.home.permission.rememberLocationPermissionHandler

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionHandler = rememberLocationPermissionHandler(
        onPermissionResult = viewModel::onLocationPermissionResult
    )

    HomeContent(
        state = state,
        onGoOnline = viewModel::onGoOnline,
        onGoOffline = viewModel::onGoOffline,
        onActionErrorShown = viewModel::onActionErrorShown,
        onRetryLoadDriver = viewModel::retryLoadDriver,
        onRetryLoadStatus = viewModel::retryLoadStatus,
        onOpenLocationSettings = permissionHandler.onOpenLocationSettings,
        onOpenAppPermissionSettings = permissionHandler.onOpenAppPermissionSettings
    )
}
