package com.master.transportes.driver.feature.auth.presentation.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit = {}
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.navigationEvent.collect {
                onNavigateToHome()
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