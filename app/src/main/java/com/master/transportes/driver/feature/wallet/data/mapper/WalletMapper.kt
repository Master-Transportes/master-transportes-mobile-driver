package com.master.transportes.driver.feature.wallet.data.mapper

import com.master.transportes.driver.feature.wallet.data.dto.WalletResponseDto
import com.master.transportes.driver.feature.wallet.domain.model.Wallet

fun WalletResponseDto.toDomain(): Wallet {
    return Wallet(
        balanceInCents = balanceInCents,
        currency = currency,
    )
}