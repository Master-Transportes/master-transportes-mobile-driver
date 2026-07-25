package com.master.transportes.driver.feature.main.presentation.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.master.transportes.driver.feature.activity.presentation.activity.ActivityScreen
import com.master.transportes.driver.feature.home.presentation.home.HomeScreen
import com.master.transportes.driver.feature.profile.presentation.profile.ProfileScreen
import com.master.transportes.driver.navigation.MainRoutes
import com.master.transportes.driver.ui.theme.BottomNavBorder
import com.master.transportes.driver.ui.theme.BottomNavSelected
import com.master.transportes.driver.ui.theme.BottomNavUnselected
import com.master.transportes.driver.ui.theme.MasterTransportesMobileDriverTheme

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(
        MainRoutes.Home.route,
        "Home",
        Icons.Default.Home
    ),
    BottomNavItem(
        MainRoutes.Activity.route,
        "Activity",
        Icons.AutoMirrored.Filled.List
    ),
    BottomNavItem(
        MainRoutes.Profile.route,
        "Profile",
        Icons.Default.Person
    )
)

@Composable
fun MainContent(
    navController: NavHostController,
    onNavigateToLogin: () -> Unit
) {
    val backStackEntry by navController
        .currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry?.destination?.route

    val darkTheme = isSystemInDarkTheme()

    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider(
                    color = BottomNavBorder,
                    thickness = 1.dp
                )
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {

                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        val contentColor = if (isSelected) {
                            if (darkTheme) BottomNavSelected else MaterialTheme.colorScheme.primary
                        } else {
                            if (darkTheme) BottomNavUnselected else MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Column(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    navController.navigate(item.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(
                                            navController.graph.startDestinationId
                                        ) {
                                            saveState = true
                                        }
                                    }
                                }
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp),
                                tint = contentColor
                            )
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = contentColor
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = MainRoutes.Home.route,
            modifier = Modifier.padding(padding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            composable(MainRoutes.Home.route) {
                HomeScreen()
            }
            composable(MainRoutes.Activity.route) {
                ActivityScreen()
            }
            composable(MainRoutes.Profile.route) {
                ProfileScreen(
                    onNavigateToLogin = onNavigateToLogin
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MasterTransportesMobileDriverTheme {
        MainContent(
            navController = rememberNavController(),
            onNavigateToLogin = {}
        )
    }
}