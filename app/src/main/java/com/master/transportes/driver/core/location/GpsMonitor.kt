package com.master.transportes.driver.core.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GpsMonitor @Inject constructor(
    @param:ApplicationContext private val context: Context
){
    private val locationManager: LocationManager? = ContextCompat.getSystemService(
        context,
        LocationManager::class.java
    )

    private val _isGpsEnabled: MutableStateFlow<Boolean> = MutableStateFlow(
        locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
    )

    val isGpsEnabled: StateFlow<Boolean> = _isGpsEnabled.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                _isGpsEnabled.value = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
            }
        }
    }

    init {
        // é uma classe utilitária do Android que permite usar recursos da classe Context de forma compatível com versões antigas do sistema
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

}