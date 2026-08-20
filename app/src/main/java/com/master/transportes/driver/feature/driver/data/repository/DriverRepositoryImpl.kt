package com.master.transportes.driver.feature.driver.data.repository

import com.master.transportes.driver.core.repository.BaseRepository
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.core.session.SessionManager
import com.master.transportes.driver.feature.driver.data.datasource.DriverRemoteDataSource
import com.master.transportes.driver.feature.driver.data.local.dao.DriverDao
import com.master.transportes.driver.feature.driver.data.mapper.toDomain
import com.master.transportes.driver.feature.driver.data.mapper.toEntity
import com.master.transportes.driver.feature.driver.domain.model.Driver
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DriverRepositoryImpl @Inject constructor(
    private val remote: DriverRemoteDataSource,
    private val driverDao: DriverDao,
    sessionManager: SessionManager
) : BaseRepository(sessionManager), DriverRepository {

    override fun observeDriver(): Flow<Driver?> = driverDao.observeDriver().map { it?.toDomain() }

    override suspend fun refreshDriver(): ApiResult<Unit> = safeApiCall {
        driverDao.upsert(remote.getMe().toEntity())
    }

    override suspend fun getStatus(): ApiResult<Boolean> = safeApiCall {
        remote.getStatus().online
    }

    override suspend fun goOnline(): ApiResult<Boolean> = safeApiCall {
        remote.goOnline().online
    }

    override suspend fun goOffline(): ApiResult<Boolean> = safeApiCall {
        remote.goOffline().online
    }

    override suspend fun clearDriver() {
        driverDao.clear()
    }
}