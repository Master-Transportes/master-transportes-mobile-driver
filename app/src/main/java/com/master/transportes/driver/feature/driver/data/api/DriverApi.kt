package com.master.transportes.driver.feature.driver.data.api

import com.master.transportes.driver.feature.driver.data.dto.DriverProfileResponseDto
import retrofit2.http.GET

interface DriverApi {

    @GET("driver/me")
    suspend fun getMe(): DriverProfileResponseDto
}