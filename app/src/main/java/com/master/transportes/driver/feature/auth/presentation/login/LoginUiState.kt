package com.master.transportes.driver.feature.auth.presentation.login

data class LoginUiState(
    val login: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginError: String? = null,
    val passwordError: String? = null,
)
