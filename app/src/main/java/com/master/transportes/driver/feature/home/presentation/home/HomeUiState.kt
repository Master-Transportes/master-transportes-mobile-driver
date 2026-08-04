package com.master.transportes.driver.feature.home.presentation.home

import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.feature.driver.domain.model.Driver

data class HomeUiState(
    val driver: Driver? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null
)