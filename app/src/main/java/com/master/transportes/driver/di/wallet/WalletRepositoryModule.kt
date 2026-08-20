package com.master.transportes.driver.di.wallet

import com.master.transportes.driver.feature.wallet.data.repository.WalletRepositoryImpl
import com.master.transportes.driver.feature.wallet.domain.repository.WalletRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WalletRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        repository: WalletRepositoryImpl
    ): WalletRepository
}