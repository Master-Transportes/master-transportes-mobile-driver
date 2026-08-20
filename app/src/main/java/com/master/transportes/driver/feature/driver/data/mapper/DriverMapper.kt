package com.master.transportes.driver.feature.driver.data.mapper

import com.master.transportes.driver.feature.driver.data.dto.DriverProfileResponseDto
import com.master.transportes.driver.feature.driver.data.local.entity.DriverEntity
import com.master.transportes.driver.feature.driver.domain.model.Driver
import com.master.transportes.driver.feature.driver.domain.model.DriverStatus

/**
 * Conversões entre DTO (API), Entity (Room) e Domain (negócio).
 *
 * O Domain nunca conhece DTO nem Entity: é o destino final de todas as
 * conversões e a única forma de dados que a UI consome.
 */

fun DriverProfileResponseDto.toDomain(): Driver {
    return Driver(
        id = id,
        fullName = fullName,
        email = email,
        status = status.toDriverStatus(),
        rejectionReason = rejectionReason,
        banReason = banReason,
        balanceInCents = balance
    )
}

fun DriverProfileResponseDto.toEntity(): DriverEntity {
    return DriverEntity(
        id = id,
        fullName = fullName,
        email = email,
        status = status,
        rejectionReason = rejectionReason,
        banReason = banReason,
        balanceInCents = balance
    )
}

fun DriverEntity.toDomain(): Driver {
    return Driver(
        id = id,
        fullName = fullName,
        email = email,
        status = status.toDriverStatus(),
        rejectionReason = rejectionReason,
        banReason = banReason,
        balanceInCents = balanceInCents
    )
}

private fun String.toDriverStatus(): DriverStatus =
    DriverStatus.entries.firstOrNull { it.name == this } ?: DriverStatus.UNKNOWN