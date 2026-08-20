package com.master.transportes.driver.feature.wallet.domain.model

/**
 * Saldo da carteira do motorista.
 *
 * Diferente do perfil (Driver), a carteira é volátil e sensível: ela NÃO é
 * persistida no Room. Vive apenas em memória no WalletStore (decisão de
 * arquitetura, análoga ao status online/offline).
 */
data class Wallet(
    val balanceInCents: Long,
    val currency: String = "BRL",
)