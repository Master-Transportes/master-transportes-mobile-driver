package com.master.transportes.driver.feature.home.presentation.home.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings

@Composable
internal fun HomeMap(
    currentLocation: LatLng?,
    cameraPositionState: CameraPositionState,
    isLocationGranted: Boolean,
    modifier: Modifier = Modifier
) {
    var hasCentered by remember { mutableStateOf(false) }

    LaunchedEffect(isLocationGranted) {
        if (isLocationGranted) hasCentered = false
    }

    LaunchedEffect(isLocationGranted, currentLocation) {
        if (isLocationGranted && !hasCentered) {
            currentLocation?.let {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 16f))
                hasCentered = true
            }
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),           // ocupa a tela inteira
        cameraPositionState = cameraPositionState,   // controla zoom/posição da câmera

        properties = MapProperties(
            mapType = MapType.NORMAL,                // mapa de ruas padrão (não satélite)
            isMyLocationEnabled = isLocationGranted, // pontinho azul só se tiver permissão
            isTrafficEnabled = true,                 // linhas de trânsito ao vivo (verde/amarelo/vermelho)
        ),

        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,             // esconde botões +/− fixos
            myLocationButtonEnabled = isLocationGranted, // botão nativo só se tiver permissão
            compassEnabled = true,                   // bússola no canto superior
            mapToolbarEnabled = false,               // evita abrir Google Maps externo ao tocar em marcador
            tiltGesturesEnabled = true,              // permite inclinar o mapa com 2 dedos (visão 3D)
        ),
    )
}
