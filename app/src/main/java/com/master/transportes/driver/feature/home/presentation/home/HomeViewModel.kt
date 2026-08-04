package com.master.transportes.driver.feature.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.master.transportes.driver.core.location.GpsMonitor
import com.master.transportes.driver.core.location.LocationProvider
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val driverRepository: DriverRepository,
    private val locationProvider: LocationProvider,
    private val gpsMonitor: GpsMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var locationJob: Job? = null

    init {
        loadDriver()
        observeGps()
    }

    private fun loadDriver() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = driverRepository.getMe()) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(driver = result.data, isLoading = false) }
                is ApiResult.Error ->
                    _uiState.update { it.copy(error = result.error, isLoading = false) }
            }
        }
    }

    private fun observeGps() {
        viewModelScope.launch {
            gpsMonitor.isGpsEnabled.collect { enabled ->
                _uiState.update { it.copy(isGpsEnabled = enabled) }
            }
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(isLocationGranted = granted) }
        if (granted) startCollectingLocation() else stopCollectingLocation()
    }

    private fun startCollectingLocation() {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            locationProvider.locationUpdates.collect { location: LatLng ->
                _uiState.update { it.copy(currentLocation = location) }
            }
        }
    }

    private fun stopCollectingLocation() {
        locationJob?.cancel()
        locationJob = null
    }

    override fun onCleared() {
        locationJob?.cancel()
        super.onCleared()
    }

}