package com.master.transportes.driver.feature.driver.domain

import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.di.ApplicationScope
import com.master.transportes.driver.feature.driver.domain.model.Driver
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Estado compartilhado em memória da sessão do motorista.
 *
 * Fonte de verdade do perfil (driver) é o Room — este store NÃO mantém uma
 * cópia paralela. O driver chega aqui através do Flow observado do
 * DriverRepository (Room). O que este store adiciona é apenas o que o Room
 * não guarda:
 *
 *   - isOnline (dinâmico, somente memória)
 *   - loading e erro do driver (bootstrap/refresh)
 *   - loading e erro do status online (bootstrap/refresh)
 *
 * Pipeline: Room → Flow<Driver> + _transient (StateFlow) → combine →
 * stateIn (Eagerly) → StateFlow<DriverSession>. Um único stateIn final;
 * driver e transient permanecem flows observáveis, sem transformações
 * intermediárias em StateFlow.
 *
 * Todas as telas (Home, Profile, Wallet...) consomem um único state.
 */
@Singleton
class DriverSessionStore @Inject constructor(
    repository: DriverRepository,
    @ApplicationScope scope: CoroutineScope,
) {

    private val driver: Flow<Driver?> = repository.observeDriver()

    private val _isOnline = MutableStateFlow(false)

    private val _isDriverLoading = MutableStateFlow(true)

    private val _driverError = MutableStateFlow<AppError?>(null)

    private val _isStatusLoading = MutableStateFlow(false)

    private val _statusError = MutableStateFlow<AppError?>(null)

    /**
     * Combina apenas os campos transitórios (máximo de 5 flows por overload
     * tipado do combine). O driver entra no passo seguinte.
     */
    private data class DriverTransient(
        val isOnline: Boolean = false,
        val isDriverLoading: Boolean = true,
        val driverError: AppError? = null,
        val isStatusLoading: Boolean = false,
        val statusError: AppError? = null,
    )

    private val transient: Flow<DriverTransient> = combine(
        _isOnline,
        _isDriverLoading,
        _driverError,
        _isStatusLoading,
        _statusError,
    ) { isOnline, isDriverLoading, driverError, isStatusLoading, statusError ->
        DriverTransient(
            isOnline = isOnline,
            isDriverLoading = isDriverLoading,
            driverError = driverError,
            isStatusLoading = isStatusLoading,
            statusError = statusError,
        )
    }

    val state: StateFlow<DriverSession> = combine(driver, transient) { driver, t ->
        DriverSession(
            driver = driver,
            isOnline = t.isOnline,
            isDriverLoading = t.isDriverLoading,
            driverError = t.driverError,
            isStatusLoading = t.isStatusLoading,
            statusError = t.statusError,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = DriverSession(),
    )

    fun setOnline(isOnline: Boolean) {
        _isOnline.value = isOnline
    }

    fun setDriverLoading() {
        _isDriverLoading.update { true }
        _driverError.value = null
    }

    fun setDriverError(error: AppError?) {
        _isDriverLoading.update { false }
        _driverError.value = error
    }

    fun setStatusLoading() {
        _isStatusLoading.update { true }
        _statusError.value = null
    }

    fun setStatusError(error: AppError?) {
        _isStatusLoading.update { false }
        _statusError.value = error
    }

    /**
     * Limpa todo o estado da sessão. Chamado no logout para que nenhum dado
     * do usuário anterior vaze para a próxima conta.
     */
    fun clear() {
        _isOnline.value = false
        _isDriverLoading.value = true
        _driverError.value = null
        _isStatusLoading.value = false
        _statusError.value = null
    }
}