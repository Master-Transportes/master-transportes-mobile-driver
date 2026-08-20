package com.master.transportes.driver.feature.wallet.data.dto

data class WalletResponseDto(
    val balanceInCents: Long,
    val currency: String = "BRL",
)