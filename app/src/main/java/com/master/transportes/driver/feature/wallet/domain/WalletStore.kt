package com.master.transportes.driver.feature.wallet.domain

import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.di.ApplicationScope
import com.master.transportes.driver.feature.wallet.domain.model.Wallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Estado compartilhado em memória da carteira do motorista.
 *
 * O saldo é volátil e sensível: NÃO vive no Room (diferente do perfil).
 * Este store é o dono do saldo e expõe um único StateFlow final (Eagerly),
 * no mesmo padrão do DriverSessionStore, mas sem fonte local.
 *
 * Quem grava aqui é o SessionBootstrap (refreshWallet) e as ações locais;
 * as telas apenas observam. `clear()` é chamado no logout via
 * SessionBootstrapStarter para a próxima conta não herdar o saldo.
 */
@Singleton
class WalletStore @Inject constructor(
    @ApplicationScope scope: CoroutineScope,
) {

    private val _wallet = MutableStateFlow<Wallet?>(null)

    private val _isLoading = MutableStateFlow(true)

    private val _error = MutableStateFlow<AppError?>(null)

    data class WalletState(
        val wallet: Wallet? = null,
        val isLoading: Boolean = true,
        val error: AppError? = null,
    )

    val state: StateFlow<WalletState> = combine(
        _wallet,
        _isLoading,
        _error,
    ) { wallet, isLoading, error ->
        WalletState(
            wallet = wallet,
            isLoading = isLoading,
            error = error,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = WalletState(),
    )

    fun setWallet(wallet: Wallet) {
        _wallet.value = wallet
        _isLoading.value = false
        _error.value = null
    }

    fun setLoading() {
        _isLoading.update { true }
        _error.value = null
    }

    fun setError(error: AppError?) {
        _isLoading.update { false }
        _error.value = error
    }

    /**
     * Limpa o estado da carteira. Chamado no logout para que nenhum dado
     * do usuário anterior vaze para a próxima conta.
     */
    fun clear() {
        _wallet.value = null
        _isLoading.value = true
        _error.value = null
    }
}