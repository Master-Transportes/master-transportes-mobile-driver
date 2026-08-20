package com.master.transportes.driver.feature.driver.domain

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.master.transportes.driver.core.session.SessionManager
import com.master.transportes.driver.core.session.SessionState
import com.master.transportes.driver.di.ApplicationScope
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import com.master.transportes.driver.feature.wallet.domain.WalletStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ponta do ciclo de vida da sessão.
 *
 * Observa o SessionManager e reage:
 *
 *   - sessionState == Authenticated → bootstrap.initialize()
 *     Cobre login E cold start (sessão restaurada do DataStore) com um único
 *     gatilho. O Mutex + flags do SessionBootstrap garantem 1x.
 *
 *   - logoutEvents → limpeza do motorista (evento, não estado):
 *       repository.clearDriver() → Room limpa → Flow emite null → Store reflete
 *       store.clear()            → flags transitórias em memória
 *       walletStore.clear()      → saldo da carteira em memória
 *       bootstrap.reset()        → próxima conta refaz o bootstrap completo
 *
 *   - app volta ao foreground (ProcessLifecycleOwner.onStart) e está autenticado
 *     → bootstrap.refreshWallet()
 *     O saldo é volátil: reconsulta o backend sempre que o app retorna ao
 *     foreground, mesmo sem logout/login. O refreshWallet() público NÃO usa a
 *     flag walletFetched (ela só guarda o initialize()) e passa pelo mesmo
 *     Mutex do bootstrap — nunca duplica em paralelo.
 *
 * O logout é tratado como evento (Channel) e não via StateFlow<Unauthenticated>,
 * porque StateFlow confla estados e pode "engolir" um logout seguido de login
 * rápido (usuário A → logout → usuário B).
 *
 * A navegação continua ignorando dados do motorista: ela só pergunta
 * "authenticated?".
 */
@Singleton
class SessionBootstrapStarter @Inject constructor(
    @param:ApplicationScope private val scope: CoroutineScope,
    sessionManager: SessionManager,
    processLifecycleOwner: ProcessLifecycleOwner,
    private val bootstrap: SessionBootstrap,
    private val store: DriverSessionStore,
    private val walletStore: WalletStore,
    private val repository: DriverRepository,
) {

    init {
        scope.launch {
            sessionManager.sessionState.collect { state ->
                if (state is SessionState.Authenticated) {
                    bootstrap.initialize()
                }
            }
        }
        scope.launch {
            sessionManager.logoutEvents.collect {
                // Ordem: Room primeiro (Flow emite null), depois o estado em
                // memória e por fim o bootstrap — mantém uma única fonte de verdade.
                repository.clearDriver()
                store.clear()
                walletStore.clear()
                bootstrap.reset()
            }
        }
        processLifecycleOwner.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    scope.launch {
                        if (sessionManager.sessionState.value is SessionState.Authenticated) {
                            bootstrap.refreshWallet()
                        }
                    }
                }
            }
        )
    }
}