package com.master.transportes.driver.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.master.transportes.driver.core.session.SessionManager
import com.master.transportes.driver.core.session.SessionState
import com.master.transportes.driver.feature.auth.presentation.login.LoginScreen
import com.master.transportes.driver.feature.home.presentation.home.HomeScreen

@Composable
fun AppNavigation(sessionManager: SessionManager) {
    val navController = rememberNavController()
    val sessionState by sessionManager.sessionState.collectAsStateWithLifecycle()

    if (sessionState is SessionState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination =
        if (sessionState is SessionState.Authenticated) Routes.Home.route
        else Routes.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
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

    // Logout/sessão expirada: volta para Login.
    LaunchedEffect(sessionState) {
        if (sessionState is SessionState.Unauthenticated &&
            navController.currentDestination?.route != Routes.Login.route
        ) {
            navController.navigate(Routes.Login.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    }

}