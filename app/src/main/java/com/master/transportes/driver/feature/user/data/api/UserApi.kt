package com.master.transportes.driver.feature.user.data.api

import com.master.transportes.driver.feature.user.data.dto.UserResponseDto
import retrofit2.http.GET

interface UserApi {

    @GET("access/me")
    suspend fun getMe(): UserResponseDto
}
