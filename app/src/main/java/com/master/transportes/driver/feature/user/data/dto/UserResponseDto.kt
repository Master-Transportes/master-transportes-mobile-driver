package com.master.transportes.driver.feature.user.data.dto

data class UserResponseDto(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String,
    val status: String,
    val isActive: Boolean,
    val banReason: String? = null
)
