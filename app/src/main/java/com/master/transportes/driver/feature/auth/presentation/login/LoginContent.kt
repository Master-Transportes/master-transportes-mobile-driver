package com.master.transportes.driver.feature.auth.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.core.error.toUserMessage
import com.master.transportes.driver.ui.theme.MasterTransportesMobileDriverTheme

@Composable
fun LoginContent(
    state: LoginUiState,
    onLoginChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    val submit = state.submit

    val isLoading = submit is SubmitState.Loading
    val errorMessage = (submit as? SubmitState.Error)?.error?.toUserMessage()

    val passwordFocusRequester = remember { FocusRequester() }

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

            errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.login,
                onValueChange = onLoginChange,
                label = { Text("Login") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onLogin() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .focusRequester(passwordFocusRequester)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLogin,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("Entrar")
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

@Preview(showBackground = true)
@Composable
fun LoginLoadingPreview() {
    MasterTransportesMobileDriverTheme {
        LoginContent(
            state = LoginUiState(
                login = "driver@example.com",
                password = "123456",
                submit = SubmitState.Loading
            ),
            onLoginChange = {},
            onPasswordChange = {},
            onLogin = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginErrorPreview() {
    MasterTransportesMobileDriverTheme {
        LoginContent(
            state = LoginUiState(
                login = "driver@example.com",
                password = "senha_errada",
                submit = SubmitState.Error(
                    error = AppError.Api(
                        code = "invalid_credentials",
                        message = "Login ou senha inválidos."
                    )
                )
            ),
            onLoginChange = {},
            onPasswordChange = {},
            onLogin = {}
        )
    }
}