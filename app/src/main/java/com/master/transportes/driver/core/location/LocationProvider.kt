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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProvider @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val permissionChecker: PermissionChecker
) {

    val locationUpdates: Flow<LatLng> = callbackFlow {
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

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateDistanceMeters(10f)
            .build()

        @Suppress("MissingPermission") // seguro: permissão verificada acima
        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    private fun Location.toLatLng(): LatLng = LatLng(latitude, longitude)
}