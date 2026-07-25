package com.master.transportes.driver.navigation

sealed class Routes(
    val route: String
) {
    data object Login : Routes("login")
    data object Main : Routes("main")
}
