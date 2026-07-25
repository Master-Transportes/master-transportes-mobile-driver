package com.master.transportes.driver.feature.profile.presentation.profile

import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.feature.user.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null
)
