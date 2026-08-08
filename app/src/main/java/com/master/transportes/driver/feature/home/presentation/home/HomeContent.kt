package com.master.transportes.driver.feature.home.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.master.transportes.driver.feature.driver.domain.model.Driver
import com.master.transportes.driver.feature.driver.domain.model.DriverStatus
import com.master.transportes.driver.feature.home.presentation.home.components.HomeOverlay
import com.master.transportes.driver.feature.home.presentation.home.components.OnlineStatusBar
import com.master.transportes.driver.feature.home.presentation.home.map.HomeMap
import com.master.transportes.driver.ui.theme.MasterTransportesMobileDriverTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeUiState,
    onOpenLocationSettings: () -> Unit = {},
    onOpenAppPermissionSettings: () -> Unit = {},
    onGoOnline: () -> Unit = {},
    onGoOffline: () -> Unit = {},
    onActionErrorShown: () -> Unit = {}
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-23.5505, -46.6333), 12f)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberBottomSheetScaffoldState()

    val navBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val density = LocalDensity.current
    val windowHeightPx = LocalWindowInfo.current.containerSize.height
    val peekHeightPx = with(density) { (56.dp + navBarInset).toPx() }
    val sheetOffsetPx = remember {
        derivedStateOf {
            runCatching { scaffoldState.bottomSheetState.requireOffset() }
                .getOrNull() ?: (windowHeightPx - peekHeightPx)
        }
    }.value
    val overlayExtraOffset = with(density) {
        (windowHeightPx - sheetOffsetPx - peekHeightPx).coerceAtLeast(0f).toDp()
    }

    LaunchedEffect(state.actionErrorMessage) {
        state.actionErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onActionErrorShown()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 56.dp + navBarInset,
        sheetDragHandle = null,
        sheetSwipeEnabled = false,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetShape = BottomSheetDefaults.HiddenShape,
        sheetContent = {
            OnlineStatusBar(
                isOnline = state.isOnline,
                onGoOffline = onGoOffline,
                sheetState = scaffoldState.bottomSheetState
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            HomeMap(
                currentLocation = state.currentLocation,
                cameraPositionState = cameraPositionState,
                isLocationGranted = state.isLocationGranted
            )

            HomeOverlay(
                state = state,
                onOpenLocationSettings = onOpenLocationSettings,
                onOpenAppPermissionSettings = onOpenAppPermissionSettings,
                onGoOnline = onGoOnline,
                bottomOffset = overlayExtraOffset
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
                isGpsEnabled = true,
                isChangingOnlineStatus = false
            )
        )
    }
}
