package com.master.transportes.driver.feature.home.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.master.transportes.driver.core.error.toUserMessage
import com.master.transportes.driver.feature.home.presentation.home.HomeUiState
import com.master.transportes.driver.feature.home.presentation.home.LocationUiState
import com.master.transportes.driver.feature.home.presentation.home.OnlineStatusUiState
import com.master.transportes.driver.feature.home.presentation.home.isChanging
import com.master.transportes.driver.feature.home.presentation.home.isOnline

@Composable
internal fun BottomSection(
    state: HomeUiState,
    onOpenLocationSettings: () -> Unit,
    onOpenAppPermissionSettings: () -> Unit,
    onGoOnline: () -> Unit,
    onRetryLoadStatus: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Só renderiza ações e banners quando há motorista carregado.
    if (state !is HomeUiState.Success) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActionArea(
            onlineStatus = state.onlineStatus,
            onGoOnline = onGoOnline
        )

        BannerArea(
            onlineStatus = state.onlineStatus,
            location = state.location,
            onOpenLocationSettings = onOpenLocationSettings,
            onOpenAppPermissionSettings = onOpenAppPermissionSettings,
            onRetryLoadStatus = onRetryLoadStatus
        )
    }
}

@Composable
private fun ActionArea(
    onlineStatus: OnlineStatusUiState,
    onGoOnline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        OnlineActionButton(
            isOnline = onlineStatus.isOnline,
            onGoOnline = onGoOnline,
            enabled = !onlineStatus.isChanging,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun BannerArea(
    onlineStatus: OnlineStatusUiState,
    location: LocationUiState,
    onOpenLocationSettings: () -> Unit,
    onOpenAppPermissionSettings: () -> Unit,
    onRetryLoadStatus: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (onlineStatus as? OnlineStatusUiState.Error)?.let { statusError ->
            StatusErrorBanner(
                message = statusError.error.toUserMessage(),
                onRetry = onRetryLoadStatus
            )
        }

        if (!location.isGranted) {
            PermissionBanner(onClick = onOpenAppPermissionSettings)
        }

        if (!location.isGpsEnabled) {
            GpsBanner(onClick = onOpenLocationSettings)
        }
    }
}