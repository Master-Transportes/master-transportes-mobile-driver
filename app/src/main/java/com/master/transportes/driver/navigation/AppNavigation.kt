package com.master.transportes.driver.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.master.transportes.driver.core.session.SessionManager
import com.master.transportes.driver.feature.auth.presentation.login.LoginScreen
import com.master.transportes.driver.feature.home.presentation.home.HomeScreen

@Composable
fun AppNavigation(sessionManager: SessionManager) {
    val navController = rememberNavController()
    val isLoggedIn by sessionManager.isLoggedIn.collectAsStateWithLifecycle()
    var startAssigned by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        if (!startAssigned) {
            startAssigned = true
            if (isLoggedIn) {
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
                }
            }
        } else if (!isLoggedIn) {
            navController.navigate(Routes.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Home.route) {
            HomeScreen()
        }
    }
}