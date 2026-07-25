package com.master.transportes.driver.feature.auth.data.mapper

import com.master.transportes.driver.feature.auth.data.dto.LoginResponseDto
import com.master.transportes.driver.core.session.Session

fun LoginResponseDto.toDomain(): Session {
    return Session(
        token = accessToken,
        expiresIn = expiresIn.toLong()
    )
}