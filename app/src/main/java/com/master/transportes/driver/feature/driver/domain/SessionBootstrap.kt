package com.master.transportes.driver.feature.driver.domain

import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Dono único das chamadas de inicialização da sessão do motorista.
 *
 * Responsabilidades:
 *   - getMe (via refreshDriver) → grava no Room → Flow atualiza todas as telas
 *   - getStatus → atualiza o isOnline do DriverSessionStore (memória)
 *
 * Proteção:
 *   - Um único Mutex serializa initialize(), refreshDriver(), refreshStatus()
 *     e reset(). Qualquer combinação destes nunca roda em paralelo, então
 *     GET /me e GET /status não duplicam entre bootstrap, retry e logout.
 *   - driverFetched/statusFetched independentes: se um dos dois falhar, o
 *     outro não é refeito à toa e o que falhou pode ser tentado de novo.
 *
 * Regra: nenhuma tela é dona de getMe()/getStatus() — este bootstrap é dono.
 */
@Singleton
class SessionBootstrap @Inject constructor(
    private val repository: DriverRepository,
    private val store: DriverSessionStore,
) {

    private val mutex = Mutex()

    /** Driver já sincronizado com sucesso (não refaz getMe à toa). */
    private var driverFetched = false

    /** Status online já lido com sucesso (não refaz getStatus à toa). */
    private var statusFetched = false

    /**
     * Inicializa a sessão: getMe e getStatus em paralelo, uma única vez por
     * sucesso. Idempotente e à prova de concorrência (login + cold start).
     */
    suspend fun initialize() = mutex.withLock {
        coroutineScope {
            if (!driverFetched) {
                launch { refreshDriverInternal() }
            }
            if (!statusFetched) {
                launch { refreshStatusInternal() }
            }
        }
    }

    /**
     * Sincroniza o perfil (GET /me → Room). Usado no retry das telas. Passa
     * pelo mesmo Mutex do bootstrap: se uma inicialização estiver em andamento,
     * o retry espera a vez e não duplica a chamada.
     */
    suspend fun refreshDriver() {
        mutex.withLock { refreshDriverInternal() }
    }

    /** Lê o status online (GET /status → memória). Usado no retry das telas. */
    suspend fun refreshStatus() {
        mutex.withLock { refreshStatusInternal() }
    }

    /**
     * Zera o controle de inicialização. Chamado no logout (via logoutEvents)
     * para que uma nova conta refaça o bootstrap completo. Serializado pelo
     * mesmo Mutex para não colidir com uma initialize() em andamento.
     */
    suspend fun reset() {
        mutex.withLock {
            driverFetched = false
            statusFetched = false
        }
    }

    private suspend fun refreshDriverInternal() {
        store.setDriverLoading()
        when (val result = repository.refreshDriver()) {
            is ApiResult.Success -> {
                driverFetched = true
                store.setDriverError(null)
            }

            is ApiResult.Error -> {
                driverFetched = false
                store.setDriverError(result.error)
            }
        }
    }

    private suspend fun refreshStatusInternal() {
        store.setStatusLoading()
        when (val result = repository.getStatus()) {
            is ApiResult.Success -> {
                statusFetched = true
                store.setOnline(result.data)
                store.setStatusError(null)
            }

            is ApiResult.Error -> {
                statusFetched = false
                store.setStatusError(result.error)
            }
        }
    }
}