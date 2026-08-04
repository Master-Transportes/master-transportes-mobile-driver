package com.master.transportes.driver.feature.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.feature.driver.domain.repository.DriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val driverRepository: DriverRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDriver()
    }

    private fun loadDriver() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = driverRepository.getMe()) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(driver = result.data, isLoading = false) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(error = result.error, isLoading = false) }
                }
            }
        }
    }
}