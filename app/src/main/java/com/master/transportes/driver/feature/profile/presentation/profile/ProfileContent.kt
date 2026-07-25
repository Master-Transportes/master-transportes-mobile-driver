package com.master.transportes.driver.feature.profile.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.feature.user.domain.model.User
import com.master.transportes.driver.ui.theme.MasterTransportesMobileDriverTheme

@Composable
fun ProfileContent(
    state: ProfileUiState,
    onLogout: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator()
                }
                state.error != null -> {
                    state.error?.let { error ->
                        val message = when (error) {
                            is AppError.Api -> error.message
                            is AppError.Network, is AppError.Timeout, is AppError.SSL -> "Sem conexÃ£o com a internet."
                            else -> "Erro inesperado. Tente novamente."
                        }
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                state.user != null -> {
                    state.user?.let { user ->
                        Text(
                            text = "OlÃ¡, ${user.fullName}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text("Sair")
            }
        }
    }
}

private val previewProfileState = ProfileUiState(
    user = User(
        id = "1",
        fullName = "Enderson Alves Da Silva",
        email = "enderson@email.com",
        role = "DRIVER",
        status = "ONLINE",
        isActive = true,
        banReason = null
    ),
    isLoading = false,
    error = null
)

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    MasterTransportesMobileDriverTheme {
        ProfileContent(
            state = previewProfileState,
            onLogout = {}
        )
    }
}
