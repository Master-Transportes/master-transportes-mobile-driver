package com.master.transportes.driver.feature.wallet.data.api

import com.master.transportes.driver.feature.wallet.data.dto.WalletResponseDto
import retrofit2.http.GET

interface WalletApi {

    @GET("driver/wallet")
    suspend fun getWallet(): WalletResponseDto
}