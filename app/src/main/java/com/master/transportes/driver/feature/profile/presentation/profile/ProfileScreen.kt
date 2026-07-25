package com.master.transportes.driver.feature.profile.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.master.transportes.driver.navigation.NavigationEvent

@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit = {}
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToLogin -> onNavigateToLogin()
                else -> {}
            }
        }
    }

    ProfileContent(
        state = state,
        onLogout = viewModel::logout
    )
}
