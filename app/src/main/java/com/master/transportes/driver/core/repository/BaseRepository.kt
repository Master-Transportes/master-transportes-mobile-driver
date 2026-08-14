package com.master.transportes.driver.core.repository

import com.master.transportes.driver.core.error.ErrorMapper
import com.master.transportes.driver.core.result.ApiResult
import com.master.transportes.driver.core.session.SessionManager
import kotlinx.coroutines.CancellationException

/**
 * Todo Repository que chama a API pode lançar exceções.
 *
 * safeApiCall centraliza o try/catch para que cada RepositoryImpl
 * não precise repetir esse bloco. Toda exceção passa pelo ErrorMapper
 * e é convertida em AppError antes de chegar ao ViewModel.
 *
 * O método é protected porque só as implementações de Repository
 * (dentro de feature/{feature}/data/repository/) devem chamá-lo.
 */
abstract class BaseRepository(
    private val sessionManager: SessionManager
) {

    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): ApiResult<T> = try {
        ApiResult.Success(apiCall())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        val error = ErrorMapper.map(e)
        sessionManager.handleSessionExpired(error)
        ApiResult.Error(error)
    }
}
