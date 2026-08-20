package com.master.transportes.driver.feature.wallet.data.datasource

import com.master.transportes.driver.feature.wallet.data.api.WalletApi
import com.master.transportes.driver.feature.wallet.data.dto.WalletResponseDto
import javax.inject.Inject

/**
 * Único ponto de contato da feature wallet com o Retrofit.
 *
 * O Repository nunca conhece a API diretamente — ele só sabe
 * que existe alguém que fornece dados remotos. Se o Retrofit
 * for trocado por Ktor ou outra lib, apenas este arquivo muda.
 */
class WalletRemoteDataSource @Inject constructor(
    private val api: WalletApi
) {

    suspend fun getWallet(): WalletResponseDto {
        return api.getWallet()
    }
}