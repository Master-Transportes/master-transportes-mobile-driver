package com.master.transportes.driver.feature.wallet.data.repository

import com.master.transportes.driver.core.repository.BaseRepository
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.core.session.SessionManager
import com.master.transportes.driver.feature.wallet.data.datasource.WalletRemoteDataSource
import com.master.transportes.driver.feature.wallet.data.mapper.toDomain
import com.master.transportes.driver.feature.wallet.domain.model.Wallet
import com.master.transportes.driver.feature.wallet.domain.repository.WalletRepository
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val remote: WalletRemoteDataSource,
    sessionManager: SessionManager
) : BaseRepository(sessionManager), WalletRepository {

    override suspend fun refreshWallet(): ApiResult<Wallet> = safeApiCall {
        remote.getWallet().toDomain()
    }
}