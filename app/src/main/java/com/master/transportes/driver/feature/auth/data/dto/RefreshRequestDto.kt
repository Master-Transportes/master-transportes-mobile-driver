package com.master.transportes.driver.feature.auth.data.dto

data class RefreshRequestDto(
    val refreshToken: String,
    val sessionId: String
)
