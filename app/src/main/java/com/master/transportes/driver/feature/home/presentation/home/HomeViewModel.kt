package com.master.transportes.driver.feature.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.master.transportes.driver.core.location.GpsMonitor
import com.master.transportes.driver.core.location.LocationProvider
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.driver.domain.DriverSession
import com.master.transportes.driver.feature.driver.domain.DriverSessionStore
import com.master.transportes.driver.feature.driver.domain.SessionBootstrap
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import com.master.transportes.driver.feature.wallet.domain.WalletStore
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
    private val driverSessionStore: DriverSessionStore,
    private val walletStore: WalletStore,
    private val sessionBootstrap: SessionBootstrap,
    private val locationProvider: LocationProvider,
    private val gpsMonitor: GpsMonitor
) : ViewModel() {

    /**
     * O motorista e o status online vêm do DriverSessionStore (fonte única,
     * alimentada pelo Room + bootstrap). A Home NÃO faz getMe()/getStatus():
     * ela apenas observa o estado compartilhado.
     *
     * O que continua local é o transitório da ação online/offline (botão
     * "alterando") e a localização/GPS.
     */

    /** true enquanto uma ação goOnline/goOffline está em andamento. */
    private val _onlineActionChanging = MutableStateFlow(false)

    private val _location = MutableStateFlow(LocationUiState())

    val uiState: StateFlow<HomeUiState> = combine(
        driverSessionStore.state,
        walletStore.state,
        _onlineActionChanging,
        _location,
    ) { session, walletState, actionChanging, location ->
        val onlineStatus =
            if (actionChanging) {
                OnlineStatusUiState.Loading(deriveOnlineStatus(session))
            } else {
                deriveOnlineStatus(session)
            }

        when {
            session.driver == null &&
                session.isDriverLoading &&
                session.driverError == null -> HomeUiState.Loading

            session.driver == null &&
                session.driverError != null -> HomeUiState.Error(session.driverError)

            session.driver != null -> HomeUiState.Success(
                driver = session.driver,
                onlineStatus = onlineStatus,
                location = location,
                wallet = walletState.wallet,
            )

            else -> HomeUiState.Loading
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
        observeGps()
        observeLocation()
    }

    /**
     * Mapeia o estado do store para o OnlineStatusUiState da tela, preservando
     * a UX atual: erro mantém o último status conhecido (session.isOnline) e
     * o botão continua funcionando a partir dele.
     */
    private fun deriveOnlineStatus(session: DriverSession): OnlineStatusUiState =
        when {
            session.statusError != null ->
                OnlineStatusUiState.Error(session.statusError, session.isOnline)

            session.isOnline -> OnlineStatusUiState.Online
            else -> OnlineStatusUiState.Offline
        }

    /** Re-tenta o carregamento do perfil (só o driver; o status não é refeito). */
    fun retryLoadDriver() {
        viewModelScope.launch { sessionBootstrap.refreshDriver() }
    }

    /** Re-tenta o carregamento do status online (só o status; o driver não é refeito). */
    fun retryLoadStatus() {
        viewModelScope.launch { sessionBootstrap.refreshStatus() }
    }

    fun onGoOnline() {
        if (_onlineActionChanging.value) return

        _onlineActionChanging.value = true

        viewModelScope.launch {
            when (val result = driverRepository.goOnline()) {
                is ApiResult.Success ->
                    if (result.data) {
                        driverSessionStore.setOnline(true)
                        _onlineActionChanging.value = false
                    } else {
                        _onlineActionChanging.value = false
                        _actionErrorMessage.send("Não foi possível ficar online agora. Tente novamente.")
                    }

                is ApiResult.Error -> {
                    _onlineActionChanging.value = false
                    _actionErrorMessage.send("Não foi possível ficar online. Tente novamente.")
                }
            }
        }
    }

    fun onGoOffline() {
        if (_onlineActionChanging.value) return

        _onlineActionChanging.value = true

        viewModelScope.launch {
            when (val result = driverRepository.goOffline()) {
                is ApiResult.Success ->
                    if (!result.data) {
                        driverSessionStore.setOnline(false)
                        _onlineActionChanging.value = false
                    } else {
                        _onlineActionChanging.value = false
                        _actionErrorMessage.send("Não foi possível ficar offline agora. Tente novamente.")
                    }

                is ApiResult.Error -> {
                    _onlineActionChanging.value = false
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