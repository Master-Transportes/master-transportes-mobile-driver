package com.master.transportes.driver.feature.home.presentation.home.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    isFollowing: Boolean,
    cameraPositionState: CameraPositionState,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(isFollowing, currentLocation) {
        if (isFollowing) {
            currentLocation?.let {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 16f))
            }
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),           // ocupa a tela inteira
        cameraPositionState = cameraPositionState,   // controla zoom/posição da câmera

        properties = MapProperties(
            mapType = MapType.NORMAL,                // mapa de ruas padrão (não satélite)
            isMyLocationEnabled = true,              // pontinho azul no mapa
            isTrafficEnabled = true,                 // linhas de trânsito ao vivo (verde/amarelo/vermelho)
        ),

        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,             // esconde botões +/− fixos
            myLocationButtonEnabled = false,         // esconde botão "me localizar" do Google (já tem FAB próprio)
            compassEnabled = true,                   // bússola no canto superior
            mapToolbarEnabled = false,               // evita abrir Google Maps externo ao tocar em marcador
            tiltGesturesEnabled = true,              // permite inclinar o mapa com 2 dedos (visão 3D)
        ),
    )
}
