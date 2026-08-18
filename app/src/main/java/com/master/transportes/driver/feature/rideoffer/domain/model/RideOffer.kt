package com.master.transportes.driver.feature.rideoffer.domain.model

data class RidePoint(
    val name: String,
    val lat: Double,
    val lng: Double
)

data class RideOffer(
    val offerId: String,
    val rideId: String,
    val origin: RidePoint,
    val destination: RidePoint,
    val offerExpiresAt: String,
    val timestamp: String
)