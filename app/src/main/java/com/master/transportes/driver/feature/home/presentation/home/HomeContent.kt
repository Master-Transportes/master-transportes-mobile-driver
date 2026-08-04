package com.master.transportes.driver.feature.home.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.ui.theme.MasterTransportesMobileDriverTheme

@Composable
fun HomeContent(
    state: HomeUiState,
    onOpenLocationSettings: () -> Unit = {},
    onOpenAppPermissionSettings: () -> Unit = {}
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-23.5505, -46.6333), 12f)
    }

    val currentLocation = state.currentLocation
    var isFollowing by remember { mutableStateOf(true) }

    LaunchedEffect(isFollowing) {
        if (isFollowing) {
            currentLocation?.let {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 16f))
            }
        }
    }

    LaunchedEffect(currentLocation) {
        if (isFollowing) {
            currentLocation?.let {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 16f))
            }
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapType = MapType.NORMAL,
                    isMyLocationEnabled = state.isLocationGranted,
                    // isTrafficEnabled = true
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                ),
            )

            if (state.isLocationGranted) {
                FloatingActionButton(
                    onClick = { isFollowing = !isFollowing },
                    containerColor = if (isFollowing) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = if (isFollowing) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = if (isFollowing) {
                            "Parar de seguir minha posição"
                        } else {
                            "Seguir minha posição"
                        }
                    )
                }
            }

            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.error != null -> {
                    val message = when (val error = state.error) {
                        is AppError.Api -> error.message
                        is AppError.Network, is AppError.Timeout, is AppError.SSL ->
                            "Sem conexão com a internet."
                        else -> "Erro inesperado. Tente novamente."
                    }
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    )
                }

                state.driver != null -> {
                    Text(
                        text = "Olá, ${state.driver.fullName}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    )
                }
            }

            if (!state.isLocationGranted || !state.isGpsEnabled) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!state.isLocationGranted) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenAppPermissionSettings() }
                        ) {
                            Text(
                                text = "Permissão de localização necessária. Toque para permitir.",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    if (!state.isGpsEnabled) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenLocationSettings() }
                        ) {
                            Text(
                                text = "GPS desligado. Toque aqui para ativar.",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(state = HomeUiState())
    }
}
