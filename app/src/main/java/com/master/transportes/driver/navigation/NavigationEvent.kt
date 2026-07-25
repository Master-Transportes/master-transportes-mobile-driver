package com.master.transportes.driver.navigation

sealed class NavigationEvent {
    data object NavigateToHome : NavigationEvent()
    data object NavigateToLogin : NavigationEvent()
}
