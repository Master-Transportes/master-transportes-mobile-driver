package com.master.transportes.driver.core.network

import com.master.transportes.driver.core.session.SessionManager
import com.master.transportes.driver.feature.auth.data.api.AuthApi
import com.master.transportes.driver.feature.auth.data.dto.RefreshRequestDto
import com.master.transportes.driver.feature.auth.data.mapper.toDomain
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    @param:Named("refreshAuthApi") private val authApi: AuthApi
) : Authenticator {

    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        val failedRequest = response.request

        // Guard 1: nunca refrescar a própria request de refresh.
        if (failedRequest.isRefreshRequest()) return null

        // Guard 2: sem refresh token salvo, não há o que renovar.
        // Não limpa a sessão aqui — o 401 será reportado ao chamador.
        val refreshToken = sessionManager.getRefreshToken() ?: return null

        val refreshed = runBlocking {
            refreshMutex.withLock {
                // Se outro thread já renovou o token, apenas reencaminha.
                if (sessionManager.getToken() == failedRequest.bearerToken()) {
                    refreshSession(refreshToken)
                } else {
                    true
                }
            }
        }

        if (!refreshed) return null

        val currentToken = sessionManager.getToken() ?: return null

        // Request reconstruída com o novo Bearer: application interceptors
        // não rodam de novo no retry disparado pelo Authenticator.
        return failedRequest.newBuilder()
            .header("Authorization", "Bearer $currentToken")
            .build()
    }

    private suspend fun refreshSession(refreshToken: String): Boolean {
        val sessionId = sessionManager.getSessionId() ?: return false
        return try {
            val tokens = authApi.refresh(
                RefreshRequestDto(
                    refreshToken = refreshToken,
                    sessionId = sessionId
                )
            )
            sessionManager.saveSession(tokens.toDomain())
            true
        } catch (e: HttpException) {
            // Só um 401 indica sessão inválida/inexistente → logout.
            if (e.code() == 401) {
                sessionManager.clearSession()
            }
            false
        } catch (e: IOException) {
            // Falha de rede/timeout: preserva o login, tenta na próxima request.
            false
        } catch (e: Exception) {
            sessionManager.clearSession()
            false
        }
    }

    private fun Request.isRefreshRequest(): Boolean {
        return url.encodedPath.endsWith("/driver/refresh")
    }

    private fun Request.bearerToken(): String? {
        return header("Authorization")
            ?.removePrefix("Bearer ")
            ?.takeIf { it.isNotBlank() }
    }
}
