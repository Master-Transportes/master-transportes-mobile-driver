package com.master.transportes.driver.feature.driver.data.dto

data class DriverProfileResponseDto(
    val id: String,
    val fullName: String,
    val email: String,
    val status: String,
    val rejectionReason: String? = null,
    val banReason: String? = null
)