package com.master.transportes.driver.feature.auth.data.datasource

import com.master.transportes.driver.feature.auth.data.api.AuthApi
import com.master.transportes.driver.feature.auth.data.dto.LoginRequestDto
import com.master.transportes.driver.feature.auth.data.dto.LoginResponseDto
import javax.inject.Inject

/**
 * Único ponto de contato da feature auth com o Retrofit.
 *
 * O Repository nunca conhece a API diretamente — ele só sabe
 * que existe alguém que fornece dados remotos. Se o Retrofit
 * for trocado por Ktor ou outra lib, apenas este arquivo muda.
 */
class AuthRemoteDataSource @Inject constructor(
    private val api: AuthApi
) {

    suspend fun login(body: LoginRequestDto): LoginResponseDto {
        return api.login(body)
    }
}
