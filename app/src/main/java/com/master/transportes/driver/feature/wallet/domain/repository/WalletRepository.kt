package com.master.transportes.driver.feature.wallet.domain.repository

import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.wallet.domain.model.Wallet

/**
 * Fonte de dados da carteira.
 *
 *   - refreshWallet() → GET /driver/wallet (único ponto de leitura do saldo)
 *
 * O saldo é volátil: o resultado vai para o WalletStore (memória), nunca
 * para o Room. Nenhuma tela chama a API diretamente — o SessionBootstrap
 * (e retries) são os donos.
 */
interface WalletRepository {

    suspend fun refreshWallet(): ApiResult<Wallet>
}