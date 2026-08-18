package com.master.transportes.driver.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.core.session.SessionManager
import com.master.transportes.driver.feature.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    private val _navigationEvent = Channel<Unit>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onChangeLogin(login: String) {
        _uiState.update { it.copy(login = login) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun login() {
        // Single-flight: se já está logando, ignora.
        if (_uiState.value.submit is SubmitState.Loading) return

        val login = _uiState.value.login
        val password = _uiState.value.password

        _uiState.update {
            it.copy(submit = SubmitState.Loading)
        }

        viewModelScope.launch {
            when (val result = repository.login(login = login, password = password)) {
                is ApiResult.Success -> {
                    sessionManager.saveSession(result.data)
                    // Não seta Success: sucesso é evento de navegação.
                    _navigationEvent.send(Unit)
                }

                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            submit = SubmitState.Error(
                                error = result.error,
                            )
                        )
                    }
                }
            }
        }
    }
}
