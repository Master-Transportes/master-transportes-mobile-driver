package com.master.transportes.driver.feature.user.domain.repository

import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.user.domain.model.User

interface UserRepository {

    suspend fun getMe(): ApiResult<User>
}
