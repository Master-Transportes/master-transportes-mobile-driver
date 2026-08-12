package com.master.transportes.driver.core.session

data class Session(
    val token: String,
    val refreshToken: String,
    val sessionId: String,
    val expiresIn: Long
)
