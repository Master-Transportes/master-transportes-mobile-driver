package com.master.transportes.driver.navigation

sealed class MainRoutes(val route: String) {
    data object Home : MainRoutes("home")
    data object Activity : MainRoutes("activity")
    data object Profile : MainRoutes("profile")
}
