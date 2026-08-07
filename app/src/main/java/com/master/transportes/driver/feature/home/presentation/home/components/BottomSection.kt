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
import com.master.transportes.driver.feature.home.presentation.home.HomeUiState

@Composable
internal fun BottomSection(
    state: HomeUiState,
    onToggleFollow: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    onOpenAppPermissionSettings: () -> Unit,
    onGoOnline: () -> Unit,
    onGoOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionArea(
            state = state,
            onToggleFollow = onToggleFollow,
            onGoOnline = onGoOnline,
            onGoOffline = onGoOffline
        )

        BannerArea(
            state = state,
            onOpenLocationSettings = onOpenLocationSettings,
            onOpenAppPermissionSettings = onOpenAppPermissionSettings
        )
    }
}

@Composable
private fun ActionArea(
    state: HomeUiState,
    onToggleFollow: () -> Unit,
    onGoOnline: () -> Unit,
    onGoOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        OnlineActionButton(
            isOnline = state.isOnline,
            onGoOnline = onGoOnline,
            onGoOffline = onGoOffline,
            enabled = !state.isChangingOnlineStatus,
            modifier = Modifier.align(Alignment.Center)
        )

        if (state.isLocationGranted) {
            FollowLocationFab(
                isFollowing = state.isFollowing,
                onClick = onToggleFollow,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun BannerArea(
    state: HomeUiState,
    onOpenLocationSettings: () -> Unit,
    onOpenAppPermissionSettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!state.isLocationGranted) {
            PermissionBanner(onClick = onOpenAppPermissionSettings)
        }

        if (!state.isGpsEnabled) {
            GpsBanner(onClick = onOpenLocationSettings)
        }
    }
}
