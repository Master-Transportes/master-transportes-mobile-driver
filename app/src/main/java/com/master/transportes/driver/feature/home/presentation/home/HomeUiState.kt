package com.master.transportes.driver.feature.home.presentation.home

import com.google.android.gms.maps.model.LatLng
import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.feature.driver.domain.model.Driver

/**
 * Estado da tela Home.
 *
 * Usamos sealed interface porque os estados Loading/Success/Error/Empty são
 * mutuamente exclusivos — a tela não pode estar "carregando" e "em erro" ao
 * mesmo tempo. O Kotlin obriga o `when` a tratar todos os casos (exaustivo).
 *
 * As seções independentes (status online e localização/GPS) vivem em
 * sub-estados próprios dentro de Success, pois carregam em paralelo e
 * podem falhar sem derrubar o restante da tela.
 */
sealed interface HomeUiState {

    /** Motorista ainda não carregou (primeira carga ou retry em andamento). */
    data object Loading : HomeUiState

    /** Motorista carregado — a tela renderiza o mapa com as seções independentes. */
    data class Success(
        val driver: Driver,
        val onlineStatus: OnlineStatusUiState,
        val location: LocationUiState,
    ) : HomeUiState

    /** Falha ao carregar o motorista — a tela não pode renderizar o conteúdo. */
    data class Error(val error: AppError) : HomeUiState

    /** Sem dados para exibir (reservado para futuro cenário de lista vazia). */
    data object Empty : HomeUiState
}

/**
 * Sub-estado do status online (seção independente).
 *
 * `Loading(previous)` preserva o último estado conhecido durante uma ação
 * online/offline, para o botão e a barra não piscarem "offline" no meio da
 * chamada. `Error(lastKnownOnline)` mantém o último estado conhecido para que
 * uma falha de leitura do status não mude o que o usuário vê.
 */
sealed interface OnlineStatusUiState {
    data object Unknown : OnlineStatusUiState
    data class Loading(val previous: OnlineStatusUiState) : OnlineStatusUiState
    data object Online : OnlineStatusUiState
    data object Offline : OnlineStatusUiState
    data class Error(
        val error: AppError,
        val lastKnownOnline: Boolean
    ) : OnlineStatusUiState
}

/** Estado derivado usado pela barra de status e pelo botão INICIAR. */
val OnlineStatusUiState.isOnline: Boolean
    get() = when (this) {
        is OnlineStatusUiState.Online -> true
        is OnlineStatusUiState.Offline -> false
        is OnlineStatusUiState.Error -> lastKnownOnline
        is OnlineStatusUiState.Loading -> previous.isOnline
        OnlineStatusUiState.Unknown -> false
    }

/** Estado derivado usado para desabilitar o botão durante uma ação em andamento. */
val OnlineStatusUiState.isChanging: Boolean
    get() = this is OnlineStatusUiState.Loading

/**
 * Sub-estado da localização e GPS (seção independente).
 */
data class LocationUiState(
    val isGranted: Boolean = false,
    val isGpsEnabled: Boolean = true,
    val current: LatLng? = null,
)