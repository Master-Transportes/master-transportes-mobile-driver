package com.master.transportes.driver.feature.auth.data.repository

import com.master.transportes.driver.core.repository.BaseRepository
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.auth.data.datasource.AuthRemoteDataSource
import com.master.transportes.driver.feature.auth.data.dto.LoginRequestDto
import com.master.transportes.driver.feature.auth.data.mapper.toDomain
import com.master.transportes.driver.core.session.Session
import com.master.transportes.driver.core.session.SessionManager
import com.master.transportes.driver.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remote: AuthRemoteDataSource,
    sessionManager: SessionManager
) : BaseRepository(sessionManager), AuthRepository {

    override suspend fun login(
        login: String,
        password: String
    ): ApiResult<Session> = safeApiCall {
        remote.login(
            LoginRequestDto(
                login = login,
                password = password
            )
        ).toDomain()
    }
}
