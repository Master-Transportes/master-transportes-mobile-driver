package com.master.transportes.driver.feature.auth.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.ui.theme.MasterTransportesMobileDriverTheme

@Composable
fun LoginContent(
    state: LoginUiState,
    onLoginChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Master App",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            state.error?.let { error ->
                when (error) {
                    is AppError.Api -> {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = error.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                    is AppError.Network, is AppError.Timeout, is AppError.SSL -> {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sem conexÃ£o com a internet.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                    else -> {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Erro inesperado. Tente novamente.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val loginError = (state.error as? AppError.Api)
                ?.details?.find { it.field == "login" }?.message

            OutlinedTextField(
                value = state.login,
                onValueChange = onLoginChange,
                label = { Text("Login") },
                isError = loginError != null,
                supportingText = loginError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val passwordError = (state.error as? AppError.Api)
                ?.details?.find { it.field == "password" }?.message

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                isError = passwordError != null,
                supportingText = passwordError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLogin,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                if (state.isLoading) {
                    Text("Carregando...")
                } else {
                    Text("Login")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MasterTransportesMobileDriverTheme {
        LoginContent(
            state = LoginUiState(),
            onLoginChange = {},
            onPasswordChange = {},
            onLogin = {}
        )
    }
}
