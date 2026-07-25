package com.master.transportes.driver.core.result

import com.master.transportes.driver.core.error.AppError

/**
 * Wrapper que substitui exceÃ§Ãµes no fluxo normal da aplicaÃ§Ã£o.
 *
 * Um Repository nunca lanÃ§a Exception para o ViewModel â€” isso
 * quebraria o StateFlow e obrigaria try/catch em cada chamada.
 *
 * Em vez disso, o Repository retorna:
 *   - ApiResult.Success com o dado esperado, ou
 *   - ApiResult.Error  com um AppError tipado.
 *
 * O ViewModel usa when para tratar cada caso sem nunca precisar
 * capturar exceÃ§Ãµes.
 */
sealed class ApiResult<out T> {

    data class Success<T>(
        val data: T
    ) : ApiResult<T>()

    data class Error(
        val error: AppError
    ) : ApiResult<Nothing>()
}