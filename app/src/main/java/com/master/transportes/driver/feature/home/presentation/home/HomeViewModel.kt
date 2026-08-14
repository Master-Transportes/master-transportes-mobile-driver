package com.master.transportes.driver.feature.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.master.transportes.driver.core.location.GpsMonitor
import com.master.transportes.driver.core.location.LocationProvider
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
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

    init {
        loadDriver()
        observeGps()
        loadStatus()
        observeLocation()
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

    fun retryLoadDriver() {
        loadDriver()
    }

    fun onGoOnline() {
        if (_uiState.value.isChangingOnlineStatus) return

        _uiState.update { it.copy(isChangingOnlineStatus = true, actionErrorMessage = null) }

        viewModelScope.launch {
            try {
                when (val result = driverRepository.goOnline()) {
                    is ApiResult.Success ->
                        _uiState.update { it.copy(isOnline = result.data) }
                    is ApiResult.Error ->
                        _uiState.update {
                            it.copy(actionErrorMessage = "Não foi possível ficar online. Tente novamente.")
                        }
                }
            } finally {
                _uiState.update { it.copy(isChangingOnlineStatus = false) }
            }
        }
    }

    fun onGoOffline() {
        if (_uiState.value.isChangingOnlineStatus) return

        _uiState.update { it.copy(isChangingOnlineStatus = true, actionErrorMessage = null) }

        viewModelScope.launch {
            try {
                when (val result = driverRepository.goOffline()) {
                    is ApiResult.Success ->
                        _uiState.update { it.copy(isOnline = result.data) }
                    is ApiResult.Error ->
                        _uiState.update {
                            it.copy(actionErrorMessage = "Não foi possível ficar offline. Tente novamente.")
                        }
                }
            } finally {
                _uiState.update { it.copy(isChangingOnlineStatus = false) }
            }
        }
    }

    fun onActionErrorShown() {
        _uiState.update { it.copy(actionErrorMessage = null) }
    }

    private fun loadStatus(){
        viewModelScope.launch {
            _uiState.update { it.copy(statusError = null) }
            when (val result = driverRepository.getStatus()) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(isOnline = result.data) }
                is ApiResult.Error ->
                    _uiState.update { it.copy(statusError = result.error) }
            }
        }
    }

    fun retryLoadStatus() {
        loadStatus()
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
    }

    private fun observeLocation() {
        viewModelScope.launch {
            _uiState
                .map { it.isLocationGranted }
                .distinctUntilChanged()
                .flatMapLatest { granted ->
                    if (granted) locationProvider.locationUpdates else emptyFlow()
                }
                .collect { location: LatLng ->
                    _uiState.update { it.copy(currentLocation = location) }
                }
        }
    }

}