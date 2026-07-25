package com.master.transportes.driver.feature.user.domain.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String,
    val status: String,
    val isActive: Boolean,
    val banReason: String? = null
)
