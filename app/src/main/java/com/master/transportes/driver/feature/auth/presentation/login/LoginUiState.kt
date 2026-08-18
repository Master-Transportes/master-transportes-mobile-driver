package com.master.transportes.driver.feature.auth.presentation.login

import com.master.transportes.driver.core.error.AppError

/**
 * Estado da tela de Login.
 *
 * Os campos de texto ficam FORA do SubmitState para que o usuário não perca
 * o que digitou quando o envio falha. O SubmitState representa apenas o
 * ciclo de envio: Idle → Loading → Error.
 */
data class LoginUiState(
    val login: String = "",
    val password: String = "",
    val submit: SubmitState = SubmitState.Idle,
)

/**
 * Ciclo de envio do formulário.
 *
 * Usamos sealed interface para o Compose/ViewModel tratar todos os casos.
 */
sealed interface SubmitState {

    /** Nenhuma tentativa de login em andamento. */
    data object Idle : SubmitState

    /** Login em andamento (desabilita o botão, mostra progresso). */
    data object Loading : SubmitState

    /** Falha no login. */
    data class Error(
        val error: AppError,
    ) : SubmitState
}