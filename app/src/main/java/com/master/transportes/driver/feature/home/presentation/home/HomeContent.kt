package com.master.transportes.driver.feature.home.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.feature.driver.domain.model.Driver
import com.master.transportes.driver.feature.driver.domain.model.DriverStatus
import com.master.transportes.driver.feature.home.presentation.home.components.FollowLocationFab
import com.master.transportes.driver.feature.home.presentation.home.components.GpsBanner
import com.master.transportes.driver.feature.home.presentation.home.components.PermissionBanner
import com.master.transportes.driver.feature.home.presentation.home.map.HomeMap
import com.master.transportes.driver.ui.components.OnlineActionButton
import com.master.transportes.driver.ui.components.OnlineStatusBar
import com.master.transportes.driver.ui.components.WalletBadge
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
                isFollowing = state.isFollowing,
                onToggleFollow = onToggleFollow,
                onOpenLocationSettings = onOpenLocationSettings,
                onOpenAppPermissionSettings = onOpenAppPermissionSettings,
                onGoOnline = onGoOnline,
                onGoOffline = onGoOffline
            )
        }
    }
}

@Composable
private fun HomeOverlay(
    state: HomeUiState,
    isFollowing: Boolean,
    onToggleFollow: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    onOpenAppPermissionSettings: () -> Unit,
    onGoOnline: () -> Unit,
    onGoOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopSection(
            state = state,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(Modifier.weight(1f))

        CenterSection(state = state)

        Spacer(Modifier.weight(1f))

        BottomSection(
            state = state,
            isFollowing = isFollowing,
            onToggleFollow = onToggleFollow,
            onOpenLocationSettings = onOpenLocationSettings,
            onOpenAppPermissionSettings = onOpenAppPermissionSettings,
            onGoOnline = onGoOnline,
            onGoOffline = onGoOffline
        )
    }
}

@Composable
private fun TopSection(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.driver?.let { driver ->
            WalletBadge(balanceInCents = driver.balanceInCents)
        }

        state.error?.let { error ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = errorMessage(error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun CenterSection(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    if (state.isLoading) {
        CircularProgressIndicator(modifier)
    }
}

@Composable
private fun BottomSection(
    state: HomeUiState,
    isFollowing: Boolean,
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
            isOnline = state.isOnline,
            isLocationGranted = state.isLocationGranted,
            isFollowing = isFollowing,
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

@Composable
private fun ActionArea(
    isOnline: Boolean,
    isLocationGranted: Boolean,
    isFollowing: Boolean,
    onToggleFollow: () -> Unit,
    onGoOnline: () -> Unit,
    onGoOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        OnlineActionButton(
            isOnline = isOnline,
            onGoOnline = onGoOnline,
            onGoOffline = onGoOffline,
            modifier = Modifier.align(Alignment.Center)
        )

        if (isLocationGranted) {
            FollowLocationFab(
                isFollowing = isFollowing,
                onClick = onToggleFollow,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

private fun errorMessage(error: AppError): String = when (error) {
    is AppError.Api -> error.message
    is AppError.Network, is AppError.Timeout, is AppError.SSL ->
        "Sem conexão com a internet."
    else -> "Erro inesperado. Tente novamente."
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(state = HomeUiState(
            isLoading = false,
            driver = Driver(
                id="1",
                fullName = "Enderson Alves da Silva",
                email = "masterzarby@gmail.com",
                status = DriverStatus.APPROVED,
                balanceInCents = 15922L
            ),
            isOnline = false,
            isLocationGranted = true,
            isGpsEnabled = true,

        ))
    }
}
