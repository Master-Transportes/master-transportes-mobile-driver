package com.master.transportes.driver.feature.auth.data.api

import com.master.transportes.driver.feature.auth.data.dto.LoginRequestDto
import com.master.transportes.driver.feature.auth.data.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("driver/login")
    suspend fun login(
        @Body body: LoginRequestDto
    ): LoginResponseDto

}