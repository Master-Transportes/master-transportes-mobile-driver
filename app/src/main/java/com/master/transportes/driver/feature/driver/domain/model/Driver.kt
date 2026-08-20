package com.master.transportes.driver.feature.driver.domain.model

enum class DriverStatus {
    PENDING,
    APPROVED,
    REJECTED,
    SUSPENDED,
    BANNED,
    UNKNOWN
}

data class Driver(
    val id: String,
    val fullName: String,
    val email: String,
    val status: DriverStatus,
    val rejectionReason: String? = null,
    val banReason: String? = null,
)
