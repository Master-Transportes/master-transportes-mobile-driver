package com.master.transportes.driver.feature.driver.data.api

import com.master.transportes.driver.feature.driver.data.dto.DriverProfileResponseDto
import com.master.transportes.driver.feature.driver.data.dto.DriverStatusResponseDto
import com.master.transportes.driver.feature.driver.data.dto.UpdateDriverLocationDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface DriverApi {

    @GET("driver/me")
    suspend fun getMe(): DriverProfileResponseDto

    @GET("driver/status")
    suspend fun getStatus(): DriverStatusResponseDto

    @POST("driver/go-online")
    suspend fun goOnline(): DriverStatusResponseDto

    @POST("driver/go-offline")
    suspend fun goOffline(): DriverStatusResponseDto

    @PUT("driver/location")
    suspend fun updateLocation(@Body body: UpdateDriverLocationDto)
}