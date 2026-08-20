package com.master.transportes.driver.core.location

import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.master.transportes.driver.core.permission.PermissionChecker
import com.master.transportes.driver.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fluxo compartilhado de localização em tempo real.
 *
 * O callbackFlow é frio: se HomeViewModel e LocationUploader coletassem
 * diretamente, cada um registraria um requestLocationUpdates próprio, gerando
 * duplicidade de GPS e gasto de bateria. Com shareIn, apenas UM listener fica
 * ativo enquanto houver coletor (WhileSubscribed) e o fluxo é compartilhado.
 *
 * Intervalo curto (2s / 2m) porque este fluxo alimenta o mapa (alta frequência).
 * O envio para a API é throttled no LocationUploader.
 */
@Singleton
class LocationProvider @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val permissionChecker: PermissionChecker,
    @ApplicationScope private val applicationScope: CoroutineScope,
) {

    val locationUpdates: SharedFlow<LatLng> = callbackFlow {
        if (!permissionChecker.hasLocationPermission()) {
            throw SecurityException("Localização requer permissão ACCESS_FINE_LOCATION")
        }

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it.toLatLng()) }
            }
        }

        @Suppress("MissingPermission") // seguro: permissão verificada acima
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let { trySend(it.toLatLng()) }
            }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2_000L)
            .setMinUpdateDistanceMeters(2f)
            .build()

        @Suppress("MissingPermission") // seguro: permissão verificada acima
        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }.shareIn(
        scope = applicationScope,
        started = SharingStarted.WhileSubscribed(5_000),
        replay = 0,
    )

    private fun Location.toLatLng(): LatLng = LatLng(latitude, longitude)
}