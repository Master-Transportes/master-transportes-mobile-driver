package com.master.transportes.driver.feature.main.presentation.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(
    onNavigateToLogin: () -> Unit
) {
    val navController = rememberNavController()

    MainContent(
        navController = navController,
        onNavigateToLogin = onNavigateToLogin
    )
}