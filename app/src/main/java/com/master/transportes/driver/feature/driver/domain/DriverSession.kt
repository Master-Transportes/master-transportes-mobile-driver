package com.master.transportes.driver.feature.driver.domain

import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.feature.driver.domain.model.Driver

/**
 * Estado compartilhado da sessão do motorista.
 *
 * Regras de arquitetura:
 *   - driver vem EXCLUSIVAMENTE do Room (via DriverRepository.observeDriver).
 *     Não existe cópia paralela em memória.
 *   - isOnline é dinâmico e vive somente em memória (decisão de arquitetura).
 *   - loading/erro são transitórios do fluxo de bootstrap/refresh.
 */
data class DriverSession(
    val driver: Driver? = null,
    val isOnline: Boolean = false,
    val isDriverLoading: Boolean = true,
    val driverError: AppError? = null,
    val isStatusLoading: Boolean = false,
    val statusError: AppError? = null,
)