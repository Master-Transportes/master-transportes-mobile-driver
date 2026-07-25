package com.master.transportes.driver.feature.user.data.mapper

import com.master.transportes.driver.feature.user.data.dto.UserResponseDto
import com.master.transportes.driver.feature.user.domain.model.User

fun UserResponseDto.toDomain(): User {
    return User(
        id = id,
        fullName = fullName,
        email = email,
        role = role,
        status = status,
        isActive = isActive,
        banReason = banReason
    )
}
