package com.master.transportes.driver.feature.home.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.master.transportes.driver.feature.driver.domain.model.Driver
import com.master.transportes.driver.feature.driver.domain.model.DriverStatus
import com.master.transportes.driver.feature.home.presentation.home.components.HomeOverlay
import com.master.transportes.driver.feature.home.presentation.home.components.OnlineStatusBar
import com.master.transportes.driver.feature.home.presentation.home.map.HomeMap
import com.master.transportes.driver.ui.theme.MasterTransportesMobileDriverTheme

@Composable
fun HomeContent(
    state: HomeUiState,
    onOpenLocationSettings: () -> Unit = {},
    onOpenAppPermissionSettings: () -> Unit = {},
    onGoOnline: () -> Unit = {},
    onGoOffline: () -> Unit = {},
    onToggleFollow: () -> Unit = {},
    onActionErrorShown: () -> Unit = {}
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-23.5505, -46.6333), 12f)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.actionErrorMessage) {
        state.actionErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onActionErrorShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            OnlineStatusBar(
                isOnline = state.isOnline,
                onGoOnline = onGoOnline,
                onGoOffline = onGoOffline
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HomeMap(
                currentLocation = state.currentLocation,
                isFollowing = state.isFollowing,
                cameraPositionState = cameraPositionState
            )

            HomeOverlay(
                state = state,
                onToggleFollow = onToggleFollow,
                onOpenLocationSettings = onOpenLocationSettings,
                onOpenAppPermissionSettings = onOpenAppPermissionSettings,
                onGoOnline = onGoOnline,
                onGoOffline = onGoOffline
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState(
                isLoading = false,
                driver = Driver(
                    id = "1",
                    fullName = "Enderson Alves da Silva",
                    email = "masterzarby@gmail.com",
                    status = DriverStatus.APPROVED,
                    balanceInCents = 15922L
                ),
                isOnline = false,
                isLocationGranted = true,
                isGpsEnabled = true
            )
        )
    }
}
