package com.master.transportes.driver.feature.auth.presentation.login

import com.master.transportes.driver.core.error.AppError

data class LoginUiState(
    var login: String = "",
    var password: String = "",
    var isLoading: Boolean = false,
    var error: AppError? = null,
)
