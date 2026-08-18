package com.master.transportes.driver.feature.rideoffer.presentation.rideoffer

import androidx.lifecycle.ViewModel
import com.master.transportes.driver.feature.rideoffer.domain.model.RideOffer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class RideOfferViewModel @Inject constructor() : ViewModel() {

    private val _activeRideOffer = MutableStateFlow<RideOffer?>(null)
    val activeRideOffer: StateFlow<RideOffer?> = _activeRideOffer.asStateFlow()

    fun accept() {
        _activeRideOffer.value = null
    }

    fun dismiss() {
        _activeRideOffer.value = null
    }
}