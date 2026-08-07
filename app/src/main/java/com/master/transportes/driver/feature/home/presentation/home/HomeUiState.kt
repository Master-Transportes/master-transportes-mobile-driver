package com.master.transportes.driver.feature.home.presentation.home

import com.google.android.gms.maps.model.LatLng
import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.feature.driver.domain.model.Driver

data class HomeUiState(
    val driver: Driver? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val currentLocation: LatLng? = null,
    val isLocationGranted: Boolean = false,
    val isGpsEnabled: Boolean = true,
    val isOnline: Boolean = false,
    val isChangingOnlineStatus: Boolean = false,
    val isFollowing: Boolean = true,
    val actionErrorMessage: String? = null,
)