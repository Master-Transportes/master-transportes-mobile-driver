package com.master.transportes.driver.core.permission

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Estado único da permissão de localização.
 *
 * Como a permissão pode ser concedida/revogada a qualquer momento,
 * ele é observável via StateFlow. A UI (HomeViewModel) atualiza este
 * store quando recebe o resultado da solicitação de permissão, e o
 * LocationUploader o usa para decidir se pode iniciar o envio.
 */
@Singleton
class LocationPermissionStore @Inject constructor(
    private val permissionChecker: PermissionChecker,
) {

    private val _isGranted = MutableStateFlow(permissionChecker.hasLocationPermission())
    val isGranted: StateFlow<Boolean> = _isGranted.asStateFlow()

    fun updateGranted(granted: Boolean) {
        _isGranted.value = granted
    }
}