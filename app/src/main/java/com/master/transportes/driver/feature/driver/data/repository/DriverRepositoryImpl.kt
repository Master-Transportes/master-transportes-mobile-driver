package com.master.transportes.driver.feature.driver.data.repository

import com.master.transportes.driver.core.repository.BaseRepository
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.driver.data.api.DriverApi
import com.master.transportes.driver.feature.driver.data.mapper.toDomain
import com.master.transportes.driver.feature.driver.domain.model.Driver
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import javax.inject.Inject

class DriverRepositoryImpl @Inject constructor(
    private val api: DriverApi
) : BaseRepository(), DriverRepository {

    override suspend fun getMe(): ApiResult<Driver> = safeApiCall {
        api.getMe().toDomain()
    }

    override suspend fun getStatus(): ApiResult<Boolean> = safeApiCall {
        api.getStatus().online
    }

    override suspend fun goOnline(): ApiResult<Boolean> = safeApiCall {
        api.goOnline().online
    }

    override suspend fun goOffline(): ApiResult<Boolean> = safeApiCall {
        api.goOffline().online
    }

}