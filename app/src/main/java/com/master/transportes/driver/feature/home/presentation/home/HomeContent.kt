package com.master.transportes.driver.feature.home.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.feature.driver.domain.model.Driver
import com.master.transportes.driver.feature.driver.domain.model.DriverStatus
import com.master.transportes.driver.ui.theme.MasterTransportesMobileDriverTheme

@Composable
fun HomeContent(state: HomeUiState) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    CircularProgressIndicator()
                }
                state.error != null -> {
                    val message = when (val error = state.error) {
                        is AppError.Api -> error.message
                        is AppError.Network, is AppError.Timeout, is AppError.SSL ->
                            "Sem conexão com a internet."
                        else -> "Erro inesperado. Tente novamente."
                    }
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                state.driver != null -> {
                    state.driver.let { driver ->
                        Text(
                            text = "Olá, ${driver.fullName}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = driver.email,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = driver.status.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

private val previewHomeState = HomeUiState(
    driver = Driver(
        id = "1",
        fullName = "Enderson Alves Da Silva",
        email = "enderson@email.com",
        status = DriverStatus.APPROVED,
        rejectionReason = null,
        banReason = null
    ),
    isLoading = false,
    error = null
)

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    MasterTransportesMobileDriverTheme {
        HomeContent(state = previewHomeState)
    }
}