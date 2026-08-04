package com.master.transportes.driver.di.driver

import com.master.transportes.driver.feature.driver.data.repository.DriverRepositoryImpl
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DriverRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDriverRepository(
        repository: DriverRepositoryImpl
    ): DriverRepository
}