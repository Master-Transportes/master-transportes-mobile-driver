package com.master.transportes.driver.feature.driver.data.datasource

import com.master.transportes.driver.feature.driver.data.api.DriverApi
import com.master.transportes.driver.feature.driver.data.dto.DriverProfileResponseDto
import com.master.transportes.driver.feature.driver.data.dto.DriverStatusResponseDto
import javax.inject.Inject

/**
 * Único ponto de contato da feature driver com o Retrofit.
 *
 * O Repository nunca conhece a API diretamente — ele só sabe
 * que existe alguém que fornece dados remotos. Se o Retrofit
 * for trocado por Ktor ou outra lib, apenas este arquivo muda.
 */
class DriverRemoteDataSource @Inject constructor(
    private val api: DriverApi
) {

    suspend fun getMe(): DriverProfileResponseDto {
        return api.getMe()
    }

    suspend fun getStatus(): DriverStatusResponseDto {
        return api.getStatus()
    }

    suspend fun goOnline(): DriverStatusResponseDto {
        return api.goOnline()
    }

    suspend fun goOffline(): DriverStatusResponseDto {
        return api.goOffline()
    }
}
