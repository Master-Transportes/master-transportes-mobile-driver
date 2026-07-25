package com.master.transportes.driver.core.repository

import com.master.transportes.driver.core.error.ErrorMapper
import com.master.transportes.driver.core.result.ApiResult

/**
 * Todo Repository que chama a API pode lanÃ§ar exceÃ§Ãµes.
 *
 * safeApiCall centraliza o try/catch para que cada RepositoryImpl
 * nÃ£o precise repetir esse bloco. Toda exceÃ§Ã£o passa pelo ErrorMapper
 * e Ã© convertida em AppError antes de chegar ao ViewModel.
 *
 * O mÃ©todo Ã© protected porque sÃ³ as implementaÃ§Ãµes de Repository
 * (dentro de feature/{feature}/data/repository/) devem chamÃ¡-lo.
 */
abstract class BaseRepository {

    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): ApiResult<T> = try {
        ApiResult.Success(apiCall())
    } catch (e: Exception) {
        ApiResult.Error(
            error = ErrorMapper.map(e)
        )
    }
}
