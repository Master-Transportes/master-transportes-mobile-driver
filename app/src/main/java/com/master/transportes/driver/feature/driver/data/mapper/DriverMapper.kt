package com.master.transportes.driver.feature.driver.data.mapper

import com.master.transportes.driver.feature.driver.data.dto.DriverProfileResponseDto
import com.master.transportes.driver.feature.driver.domain.model.Driver
import com.master.transportes.driver.feature.driver.domain.model.DriverStatus

fun DriverProfileResponseDto.toDomain(): Driver {
    return Driver(
        id = id,
        fullName = fullName,
        email = email,
        status = status.toDriverStatus(),
        rejectionReason = rejectionReason,
        banReason = banReason
    )
}

private fun String.toDriverStatus(): DriverStatus =
    DriverStatus.entries.firstOrNull { it.name == this } ?: DriverStatus.PENDING