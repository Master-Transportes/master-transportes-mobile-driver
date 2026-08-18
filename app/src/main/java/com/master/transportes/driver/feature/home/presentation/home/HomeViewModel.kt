package com.master.transportes.driver.feature.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.master.transportes.driver.core.location.GpsMonitor
import com.master.transportes.driver.core.location.LocationProvider
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.driver.domain.model.Driver
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val driverRepository: DriverRepository,
    private val locationProvider: LocationProvider,
    private val gpsMonitor: GpsMonitor
) : ViewModel() {

    /**
     * Fontes independentes das seções da tela. Cada coroutine atualiza a sua
     * própria fonte e o `combine` monta o HomeUiState — isso elimina a condição
     * de corrida entre carregar o motorista e carregar o status online.
     */

    /** null = ainda carregando. */
    private val _driver = MutableStateFlow<ApiResult<Driver>?>(null)

    private val _onlineStatus = MutableStateFlow<OnlineStatusUiState>(OnlineStatusUiState.Unknown)

    private val _location = MutableStateFlow(LocationUiState())

    val uiState: StateFlow<HomeUiState> = combine(
        _driver,
        _onlineStatus,
        _location,
    ) { driverResult, onlineStatus, location ->
        when (driverResult) {
            null -> HomeUiState.Loading
            is ApiResult.Error -> HomeUiState.Error(driverResult.error)
            is ApiResult.Success -> HomeUiState.Success(
                driver = driverResult.data,
                onlineStatus = onlineStatus,
                location = location,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState.Loading,
    )

    /** Mensagem one-shot para o Snackbar (erros de ação online/offline). */
    private val _actionErrorMessage = Channel<String>(Channel.BUFFERED)
    val actionErrorMessage: Flow<String> = _actionErrorMessage.receiveAsFlow()

    init {
        loadDriver()
        loadStatus()
        observeGps()
        observeLocation()
    }

    private fun loadDriver() {
        viewModelScope.launch {
            _driver.value = null
            val result = driverRepository.getMe()
            _driver.value = result
        }
    }

    fun retryLoadDriver() {
        loadDriver()
    }

    private fun loadStatus() {
        viewModelScope.launch {
            when (val result = driverRepository.getStatus()) {
                is ApiResult.Success ->
                    _onlineStatus.value =
                        if (result.data) OnlineStatusUiState.Online
                        else OnlineStatusUiState.Offline
                is ApiResult.Error -> {
                    val lastKnown = _onlineStatus.value.isOnline
                    _onlineStatus.value = OnlineStatusUiState.Error(result.error, lastKnown)
                }
            }
        }
    }

    fun retryLoadStatus() {
        loadStatus()
    }

    fun onGoOnline() {
        if (_onlineStatus.value is OnlineStatusUiState.Loading) return

        val previous = _onlineStatus.value
        _onlineStatus.value = OnlineStatusUiState.Loading(previous)

        viewModelScope.launch {
            when (val result = driverRepository.goOnline()) {
                is ApiResult.Success ->
                    _onlineStatus.value = OnlineStatusUiState.Online
                is ApiResult.Error -> {
                    _onlineStatus.value = previous
                    _actionErrorMessage.send("Não foi possível ficar online. Tente novamente.")
                }
            }
        }
    }

    fun onGoOffline() {
        if (_onlineStatus.value is OnlineStatusUiState.Loading) return

        val previous = _onlineStatus.value
        _onlineStatus.value = OnlineStatusUiState.Loading(previous)

        viewModelScope.launch {
            when (val result = driverRepository.goOffline()) {
                is ApiResult.Success ->
                    _onlineStatus.value = OnlineStatusUiState.Offline
                is ApiResult.Error -> {
                    _onlineStatus.value = previous
                    _actionErrorMessage.send("Não foi possível ficar offline. Tente novamente.")
                }
            }
        }
    }

    private fun observeGps() {
        viewModelScope.launch {
            gpsMonitor.isGpsEnabled.collect { enabled ->
                _location.update { it.copy(isGpsEnabled = enabled) }
            }
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        _location.update { it.copy(isGranted = granted) }
    }

    private fun observeLocation() {
        viewModelScope.launch {
            _location
                .map { it.isGranted }
                .distinctUntilChanged()
                .flatMapLatest { granted ->
                    if (granted) locationProvider.locationUpdates else emptyFlow()
                }
                .collect { location: LatLng ->
                    _location.update { it.copy(current = location) }
                }
        }
    }
}