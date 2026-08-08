package com.master.transportes.driver.feature.home.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.feature.home.presentation.home.HomeUiState

@Composable
internal fun HomeOverlay(
    state: HomeUiState,
    onOpenLocationSettings: () -> Unit,
    onOpenAppPermissionSettings: () -> Unit,
    modifier: Modifier = Modifier,
    onGoOnline: () -> Unit,
    bottomOffset: Dp = 0.dp
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = bottomOffset),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopSection(
            state = state,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(Modifier.weight(1f))

        CenterSection(state = state)

        Spacer(Modifier.weight(1f))

        BottomSection(
            state = state,
            onOpenLocationSettings = onOpenLocationSettings,
            onOpenAppPermissionSettings = onOpenAppPermissionSettings,
            onGoOnline = onGoOnline
        )
    }
}

@Composable
private fun TopSection(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.driver?.let { driver ->
            WalletBadge(balanceInCents = driver.balanceInCents)
        }

        state.error?.let { error ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = errorMessage(error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun CenterSection(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    if (state.isLoading) {
        CircularProgressIndicator(modifier)
    }
}

private fun errorMessage(error: AppError): String = when (error) {
    is AppError.Api -> error.message
    is AppError.Network, is AppError.Timeout, is AppError.SSL ->
        "Sem conexão com a internet."
    else -> "Erro inesperado. Tente novamente."
}
