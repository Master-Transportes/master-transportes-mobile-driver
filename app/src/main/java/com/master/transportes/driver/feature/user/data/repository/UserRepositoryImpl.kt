package com.master.transportes.driver.feature.user.data.repository

import com.master.transportes.driver.core.repository.BaseRepository
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.user.data.api.UserApi
import com.master.transportes.driver.feature.user.data.mapper.toDomain
import com.master.transportes.driver.feature.user.domain.model.User
import com.master.transportes.driver.feature.user.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
) : BaseRepository(), UserRepository {

    override suspend fun getMe(): ApiResult<User> = safeApiCall {
        api.getMe().toDomain()
    }
}
