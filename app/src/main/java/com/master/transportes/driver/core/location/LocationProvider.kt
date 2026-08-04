package com.master.transportes.driver.core.location

import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProvider @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) {

    @Suppress("MissingPermission")
    val locationUpdates: Flow<LatLng> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it.toLatLng()) }
            }
        }

        // Última posição conhecida (resposta rápida)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let { trySend(it.toLatLng()) }
            }

        // Atualizações contínuas
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateDistanceMeters(10f)
            .build()
        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    private fun Location.toLatLng(): LatLng = LatLng(latitude, longitude)
}