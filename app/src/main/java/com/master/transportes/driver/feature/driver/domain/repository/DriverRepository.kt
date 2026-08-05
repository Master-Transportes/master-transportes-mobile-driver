package com.master.transportes.driver.feature.driver.domain.repository

import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.driver.domain.model.Driver

interface DriverRepository {

    suspend fun getMe(): ApiResult<Driver>

    suspend fun getStatus(): ApiResult<Boolean>

    suspend fun goOnline(): ApiResult<Boolean>

    suspend fun goOffline(): ApiResult<Boolean>

}