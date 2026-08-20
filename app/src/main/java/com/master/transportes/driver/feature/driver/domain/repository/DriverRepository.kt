package com.master.transportes.driver.feature.driver.domain.repository

import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.driver.domain.model.Driver
import kotlinx.coroutines.flow.Flow

/**
 * Fonte de dados do motorista.
 *
 * A interface reflete a arquitetura nova:
 *   - observeDriver()   → leitura observável a partir do Room (fonte local)
 *   - refreshDriver()   → sincroniza API → Room (único ponto de escrita do perfil)
 *   - getStatus()       → somente bootstrap/refresh (status online, em memória)
 *   - goOnline()/goOffline() → ações do usuário
 *   - clearDriver()     → logout
 *
 * NÃO existe mais getMe() público: cada tela não tem motivo para chamar a API
 * diretamente. O getMe do backend ficou interno ao refreshDriver().
 */
interface DriverRepository {

    fun observeDriver(): Flow<Driver?>

    suspend fun refreshDriver(): ApiResult<Unit>

    suspend fun getStatus(): ApiResult<Boolean>

    suspend fun goOnline(): ApiResult<Boolean>

    suspend fun goOffline(): ApiResult<Boolean>

    suspend fun clearDriver()
}