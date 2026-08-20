package com.master.transportes.driver.core.location

import android.os.SystemClock
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.master.transportes.driver.core.permission.LocationPermissionStore
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.di.ApplicationScope
import com.master.transportes.driver.feature.driver.domain.DriverSessionStore
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Orquestrador do envio de localização para a API.
 *
 * Só inicia o upload quando:
 *   1. O motorista está online (DriverSessionStore.isOnline)
 *   2. A permissão de localização foi concedida (LocationPermissionStore)
 *
 * O fluxo do mapa (UI) e o fluxo de envio (API) compartilham o mesmo
 * SharedFlow de localização, mas o envio é throttled para reduzir
 * chamadas e bateria. Falhas de envio são logadas (Log.w), nunca derrubam
 * o app — o safeApiCall do repositório converte em ApiResult sem lançar.
 */
@Singleton
class LocationUploader @Inject constructor(
    private val locationProvider: LocationProvider,
    private val driverRepository: DriverRepository,
    private val driverSessionStore: DriverSessionStore,
    private val locationPermissionStore: LocationPermissionStore,
    @ApplicationScope private val applicationScope: CoroutineScope,
) {

    private var uploadJob: Job? = null

    private var confirmJob: Job? = null

    init {
        observeUploadConditions()
    }

    /**
     * Envia a localização atual UMA vez, como confirmação de entrada no app.
     *
     * Chamado quando a sessão fica autenticada (login ou cold start), SEMPRE
     * após o bootstrap (getStatus) resolver o status online. Usa a primeira
     * leitura do fluxo (lastLocation costuma chegar rápido) e cancela em
     * seguida — não mantém o GPS ligado.
     *
     * Só envia se o motorista estiver OFFLINE: se já estiver online, o upload
     * contínuo (observeUploadConditions) envia a localização sozinho, e enviar
     * aqui duplicaria a primeira chamada.
     */
    fun confirmCurrentLocation() {
        if (!locationPermissionStore.isGranted.value) return
        if (driverSessionStore.state.value.isOnline) return
        if (confirmJob?.isActive == true) return

        confirmJob = applicationScope.launch {
            try {
                val location = locationProvider.locationUpdates.first()
                when (val result = driverRepository.updateLocation(location.latitude, location.longitude)) {
                    is ApiResult.Error -> {
                        Log.w("LocationUploader", "Falha ao confirmar localização: ${result.error}")
                    }
                    else -> Unit
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("LocationUploader", "Falha ao confirmar localização", e)
            }
        }
    }

    private fun observeUploadConditions() {
        applicationScope.launch {
            combine(
                driverSessionStore.state.map { it.isOnline }.distinctUntilChanged(),
                locationPermissionStore.isGranted,
            ) { online, permissionGranted ->
                online && permissionGranted
            }.distinctUntilChanged().collect { canUpload ->
                if (canUpload) start() else stop()
            }
        }
    }

    private fun start() {
        if (uploadJob?.isActive == true) return

        uploadJob = applicationScope.launch {
            locationProvider.locationUpdates
                .throttleLocation()
                .collect { latLng ->
                    when (val result = driverRepository.updateLocation(latLng.latitude, latLng.longitude)) {
                        is ApiResult.Error -> {
                            Log.w("LocationUploader", "Falha ao enviar localização: ${result.error}")
                        }
                        else -> Unit
                    }
                }
        }
    }

    private fun stop() {
        uploadJob?.cancel()
        uploadJob = null
    }

    /**
     * Envia no máximo a cada MIN_DISTANCE_METERS (25m) ou a cada
     * MIN_INTERVAL_MS (10s), o que ocorrer primeiro.
     */
    private fun Flow<LatLng>.throttleLocation(): Flow<LatLng> = flow {
        var lastLat: Double? = null
        var lastLng: Double? = null
        var lastEmitAt = 0L

        collect { latLng ->
            val now = SystemClock.elapsedRealtime()
            val shouldEmit = when {
                lastLat == null || lastLng == null -> true
                now - lastEmitAt >= MIN_INTERVAL_MS -> true
                distanceInMeters(lastLat!!, lastLng!!, latLng.latitude, latLng.longitude) >= MIN_DISTANCE_METERS -> true
                else -> false
            }

            if (shouldEmit) {
                emit(latLng)
                lastLat = latLng.latitude
                lastLng = latLng.longitude
                lastEmitAt = now
            }
        }
    }

    private fun distanceInMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        return results[0]
    }

    private companion object {
        const val MIN_DISTANCE_METERS = 25f
        const val MIN_INTERVAL_MS = 10_000L
    }
}