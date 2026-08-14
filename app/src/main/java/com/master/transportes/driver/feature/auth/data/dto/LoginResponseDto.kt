package com.master.transportes.driver.feature.auth.data.dto

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val sessionId: String,
    val expiresIn: Long
)
