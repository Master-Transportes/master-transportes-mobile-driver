package com.master.transportes.driver.feature.auth.domain.repository

import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.core.session.Session

interface AuthRepository {

    suspend fun login(
        login: String,
        password: String
    ): ApiResult<Session>

}