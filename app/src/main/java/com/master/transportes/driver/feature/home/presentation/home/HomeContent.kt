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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.master.transportes.driver.feature.rideoffer.domain.model.RideOffer
import com.master.transportes.driver.feature.rideoffer.domain.model.RidePoint
import com.master.transportes.driver.feature.rideoffer.presentation.rideoffer.RideOfferCard
import com.master.transportes.driver.feature.rideoffer.presentation.rideoffer.offerExpirationIso
import com.master.transportes.driver.ui.theme.MasterTransportesMobileDriverTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeUiState,
    snackbarHostState: SnackbarHostState,
    rideOffer: RideOffer? = null,
    onAcceptRideOffer: () -> Unit = {},
    onDismissRideOffer: () -> Unit = {},
    onOpenLocationSettings: () -> Unit = {},
    onOpenAppPermissionSettings: () -> Unit = {},
    onGoOnline: () -> Unit = {},
    onGoOffline: () -> Unit = {},
    onRetryLoadDriver: () -> Unit = {},
    onRetryLoadStatus: () -> Unit = {}
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-23.5505, -46.6333), 12f)
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 56.dp + navBarInset,
            sheetDragHandle = null,
            sheetSwipeEnabled = false,
            sheetContainerColor = MaterialTheme.colorScheme.surface,
            sheetShape = BottomSheetDefaults.HiddenShape,
            sheetContent = {
                // A barra de status online só existe quando há motorista carregado.
                if (state is HomeUiState.Success) {
                    OnlineStatusBar(
                        isOnline = state.onlineStatus.isOnline,
                        onGoOffline = onGoOffline,
                        sheetState = scaffoldState.bottomSheetState
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
            ) {
                // O mapa nunca é substituído pelos estados de loading/erro:
                // eles aparecem como overlay por cima.
                HomeMap(
                    currentLocation = (state as? HomeUiState.Success)?.location?.current,
                    cameraPositionState = cameraPositionState,
                    isLocationGranted = (state as? HomeUiState.Success)?.location?.isGranted ?: false
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

        rideOffer?.let { offer ->
            RideOfferCard(
                offer = offer,
                onAccept = onAcceptRideOffer,
                onDismiss = onDismissRideOffer,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 56.dp + navBarInset + 16.dp + overlayExtraOffset)
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
            state = HomeUiState.Loading,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ---------- 2. Erro de rede (sem driver) ----------
@Preview(showBackground = true, name = "Erro de rede")
@Composable
fun HomeNetworkErrorPreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState.Error(AppError.Network),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ---------- 3. Sem dados ----------
@Preview(showBackground = true, name = "Vazio")
@Composable
fun HomeEmptyPreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState.Empty,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ========== ESTADOS DE SUCESSO ==========

// ---------- 4. Sucesso – Motorista aprovado, online ----------
@Preview(showBackground = true, name = "Sucesso – Online")
@Composable
fun HomeSuccessOnlinePreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState.Success(
                driver = Driver(
                    id = "1",
                    fullName = "Enderson Alves da Silva",
                    email = "masterzarby@gmail.com",
                    status = DriverStatus.APPROVED,
                    balanceInCents = 15922L
                ),
                onlineStatus = OnlineStatusUiState.Online,
                location = LocationUiState(
                    isGranted = true,
                    isGpsEnabled = true,
                    current = LatLng(-23.5505, -46.6333)
                )
            ),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ---------- 5. Sucesso – Motorista aprovado, offline ----------
@Preview(showBackground = true, name = "Sucesso – Offline")
@Composable
fun HomeSuccessOfflinePreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState.Success(
                driver = Driver(
                    id = "1",
                    fullName = "Enderson Alves da Silva",
                    email = "masterzarby@gmail.com",
                    status = DriverStatus.APPROVED,
                    balanceInCents = 15922L
                ),
                onlineStatus = OnlineStatusUiState.Offline,
                location = LocationUiState()
            ),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ========== ESTADOS DO MOTORISTA (NÃO APROVADO) ==========

// ---------- 6. Motorista pendente ----------
@Preview(showBackground = true, name = "Motorista Pendente")
@Composable
fun HomePendingDriverPreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState.Success(
                driver = Driver(
                    id = "2",
                    fullName = "Maria Oliveira",
                    email = "maria@email.com",
                    status = DriverStatus.PENDING,
                    balanceInCents = 0L
                ),
                onlineStatus = OnlineStatusUiState.Offline,
                location = LocationUiState(isGranted = true, isGpsEnabled = true)
            ),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ========== ESTADOS DE PERMISSÃO / LOCALIZAÇÃO / GPS ==========

// ---------- 7. Localização negada ----------
@Preview(showBackground = true, name = "Localização negada")
@Composable
fun HomeLocationDeniedPreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState.Success(
                driver = Driver(
                    id = "1",
                    fullName = "Enderson Alves da Silva",
                    email = "masterzarby@gmail.com",
                    status = DriverStatus.APPROVED,
                    balanceInCents = 15922L
                ),
                onlineStatus = OnlineStatusUiState.Offline,
                location = LocationUiState(isGranted = false, isGpsEnabled = true)
            ),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ---------- 8. GPS desligado ----------
@Preview(showBackground = true, name = "GPS desligado")
@Composable
fun HomeGpsDisabledPreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState.Success(
                driver = Driver(
                    id = "1",
                    fullName = "Enderson Alves da Silva",
                    email = "masterzarby@gmail.com",
                    status = DriverStatus.APPROVED,
                    balanceInCents = 15922L
                ),
                onlineStatus = OnlineStatusUiState.Offline,
                location = LocationUiState(isGranted = true, isGpsEnabled = false)
            ),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ========== ESTADOS DE AÇÃO / INTERAÇÃO ==========

// ---------- 9. Alterando status online (loading da ação) ----------
@Preview(showBackground = true, name = "Alterando status online")
@Composable
fun HomeChangingOnlineStatusPreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState.Success(
                driver = Driver(
                    id = "1",
                    fullName = "Enderson Alves da Silva",
                    email = "masterzarby@gmail.com",
                    status = DriverStatus.APPROVED,
                    balanceInCents = 15922L
                ),
                onlineStatus = OnlineStatusUiState.Loading(OnlineStatusUiState.Offline),
                location = LocationUiState(isGranted = true, isGpsEnabled = true)
            ),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ---------- 10. Falha no carregamento do status online ----------
@Preview(showBackground = true, name = "Erro de status")
@Composable
fun HomeStatusErrorPreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState.Success(
                driver = Driver(
                    id = "1",
                    fullName = "Enderson Alves da Silva",
                    email = "masterzarby@gmail.com",
                    status = DriverStatus.APPROVED,
                    balanceInCents = 15922L
                ),
                onlineStatus = OnlineStatusUiState.Error(
                    error = AppError.Network,
                    lastKnownOnline = false
                ),
                location = LocationUiState(isGranted = true, isGpsEnabled = true)
            ),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ---------- 11. Mensagem de erro de ação (Snackbar) ----------
@Preview(showBackground = true, name = "Erro de ação")
@Composable
fun HomeActionErrorPreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState.Success(
                driver = Driver(
                    id = "1",
                    fullName = "Enderson Alves da Silva",
                    email = "masterzarby@gmail.com",
                    status = DriverStatus.APPROVED,
                    balanceInCents = 15922L
                ),
                onlineStatus = OnlineStatusUiState.Offline,
                location = LocationUiState(isGranted = true, isGpsEnabled = true)
            ),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

// ---------- 12. Oferta de corrida ativa ----------
@Preview(showBackground = true, name = "Oferta de corrida ativa")
@Composable
fun HomeRideOfferPreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(
            state = HomeUiState.Success(
                driver = Driver(
                    id = "1",
                    fullName = "Enderson Alves da Silva",
                    email = "masterzarby@gmail.com",
                    status = DriverStatus.APPROVED,
                    balanceInCents = 15922L
                ),
                onlineStatus = OnlineStatusUiState.Online,
                location = LocationUiState(
                    isGranted = true,
                    isGpsEnabled = true,
                    current = LatLng(-23.5505, -46.6333)
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            rideOffer = RideOffer(
                offerId = "offer_1",
                rideId = "ride_1",
                origin = RidePoint(
                    name = "Av. Doutor Teixeira de Barros, Vila Boa Vista",
                    lat = -23.5505,
                    lng = -46.6333
                ),
                destination = RidePoint(
                    name = "Rua Exemplo, 456 - Centro",
                    lat = -23.6100,
                    lng = -46.6900
                ),
                offerExpiresAt = offerExpirationIso(System.currentTimeMillis() + 20_000),
                timestamp = offerExpirationIso(System.currentTimeMillis())
            )
        )
    }
}