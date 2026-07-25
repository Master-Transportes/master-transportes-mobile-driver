package com.master.transportes.driver.feature.auth.presentation.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.master.transportes.driver.navigation.NavigationEvent

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit = {}
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToHome -> onNavigateToHome()
                is NavigationEvent.NavigateToLogin -> {}
            }
        }
    }

    LoginContent(
        state = state,
        onLoginChange = viewModel::onChangeLogin,
        onPasswordChange = viewModel::onPasswordChange,
        onLogin = viewModel::login
    )
}
