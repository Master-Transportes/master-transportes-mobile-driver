package com.master.transportes.driver.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.core.session.SessionManager
import com.master.transportes.driver.feature.auth.domain.repository.AuthRepository
import com.master.transportes.driver.navigation.NavigationEvent
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

    private val _navigationEvent = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onChangeLogin(login: String) {
        _uiState.update { it.copy(login = login) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.login(
                login = _uiState.value.login,
                password = _uiState.value.password
            )) {
                is ApiResult.Success -> {
                    sessionManager.saveSession(result.data)
                    _navigationEvent.send(NavigationEvent.NavigateToHome)
                }

                is ApiResult.Error -> {
                    _uiState.update { it.copy(error = result.error) }
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
