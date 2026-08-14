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
import com.master.transportes.driver.core.error.AppError
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
    onActionErrorShown: () -> Unit = {},
    onRetryLoadDriver: () -> Unit = {},
    onRetryLoadStatus: () -> Unit = {}
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
                onRetryLoadDriver = onRetryLoadDriver,
                onRetryLoadStatus = onRetryLoadStatus,
                bottomOffset = overlayExtraOffset
            )
        }
    }
}

// ========== ESTADOS DE CARREGAMENTO E ERRO ==========

// ---------- 1. Carregando ----------
@Preview(showBackground = true, name = "Carregando")
@Composable
fun HomeLoadingPreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState(
                isLoading = true,
                driver = null,
                isOnline = false,
                isLocationGranted = false,
                isGpsEnabled = true,
                isChangingOnlineStatus = false,
                error = null,
                currentLocation = null,
                actionErrorMessage = null,
            )
        )
    }
}

// ---------- 2. Erro de rede (sem driver) ----------
@Preview(showBackground = true, name = "Erro de rede")
@Composable
fun HomeNetworkErrorPreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState(
                isLoading = false,
                driver = null,
                error = AppError.Network,
                isOnline = false,
                isLocationGranted = false,
                isGpsEnabled = true,
                isChangingOnlineStatus = false,
                currentLocation = null,
                actionErrorMessage = null,
            )
        )
    }
}

// ========== ESTADOS DE SUCESSO ==========

// ---------- 3. Sucesso – Motorista aprovado, online ----------
@Preview(showBackground = true, name = "Sucesso – Online")
@Composable
fun HomeSuccessOnlinePreview() {
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
                isOnline = true,
                isLocationGranted = true,
                isGpsEnabled = true,
                isChangingOnlineStatus = false,
                error = null,
                currentLocation = LatLng(-23.5505, -46.6333), // São Paulo
                actionErrorMessage = null,
            )
        )
    }
}

// ---------- 4. Sucesso – Motorista aprovado, offline ----------
@Preview(showBackground = true, name = "Sucesso – Offline")
@Composable
fun HomeSuccessOfflinePreview() {
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
                isChangingOnlineStatus = false,
                error = null,
                currentLocation = null,
                actionErrorMessage = null,
            )
        )
    }
}

// ========== ESTADOS DO MOTORISTA (NÃO APROVADO) ==========

// ---------- 5. Motorista pendente ----------
@Preview(showBackground = true, name = "Motorista Pendente")
@Composable
fun HomePendingDriverPreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState(
                isLoading = false,
                driver = Driver(
                    id = "2",
                    fullName = "Maria Oliveira",
                    email = "maria@email.com",
                    status = DriverStatus.PENDING,
                    balanceInCents = 0L
                ),
                isOnline = false,
                isLocationGranted = true,
                isGpsEnabled = true,
                isChangingOnlineStatus = false,
                error = null,
                currentLocation = null,
                actionErrorMessage = null,
            )
        )
    }
}

// ========== ESTADOS DE PERMISSÃO / LOCALIZAÇÃO / GPS ==========

// ---------- 6. Localização negada ----------
@Preview(showBackground = true, name = "Localização negada")
@Composable
fun HomeLocationDeniedPreview() {
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
                isLocationGranted = false, // permissão negada
                isGpsEnabled = true,
                isChangingOnlineStatus = false,
                error = null,
                currentLocation = null,
                actionErrorMessage = null,
            )
        )
    }
}

// ---------- 7. GPS desligado ----------
@Preview(showBackground = true, name = "GPS desligado")
@Composable
fun HomeGpsDisabledPreview() {
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
                isGpsEnabled = false, // GPS desativado
                isChangingOnlineStatus = false,
                error = null,
                currentLocation = null,
                actionErrorMessage = null,
            )
        )
    }
}

// ========== ESTADOS DE AÇÃO / INTERAÇÃO ==========

// ---------- 8. Alterando status online (loading) ----------
@Preview(showBackground = true, name = "Alterando status online")
@Composable
fun HomeChangingOnlineStatusPreview() {
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
                isChangingOnlineStatus = true, // aguardando resposta do servidor
                error = null,
                currentLocation = null,
                actionErrorMessage = null,
            )
        )
    }
}

// ---------- 9. Mensagem de erro de ação (ex.: falha ao alternar status) ----------
@Preview(showBackground = true, name = "Erro de ação")
@Composable
fun HomeActionErrorPreview() {
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
                isChangingOnlineStatus = false,
                error = null,
                currentLocation = null,
                actionErrorMessage = "Não foi possível conectar. Tente novamente.",
            )
        )
    }
}