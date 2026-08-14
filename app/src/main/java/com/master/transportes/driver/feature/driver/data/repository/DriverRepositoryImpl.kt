package com.master.transportes.driver.feature.driver.data.repository

import com.master.transportes.driver.core.repository.BaseRepository
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.driver.data.datasource.DriverRemoteDataSource
import com.master.transportes.driver.feature.driver.data.mapper.toDomain
import com.master.transportes.driver.feature.driver.domain.model.Driver
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import com.master.transportes.driver.core.session.SessionManager
import javax.inject.Inject

class DriverRepositoryImpl @Inject constructor(
    private val remote: DriverRemoteDataSource,
    sessionManager: SessionManager
) : BaseRepository(sessionManager), DriverRepository {

    override suspend fun getMe(): ApiResult<Driver> = safeApiCall {
        remote.getMe().toDomain()
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

}