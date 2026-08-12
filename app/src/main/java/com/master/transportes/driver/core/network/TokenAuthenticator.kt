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

    companion object {
        private const val REFRESH_FAILURE_COOLDOWN_MS = 2_000L
    }

    private val refreshMutex = Mutex()

    private var lastRefreshAttemptFailedAt: Long = 0L

    private object RefreshRetryTag

    override fun authenticate(route: Route?, response: Response): Request? {
        val failedRequest = response.request

        // Guard 1: nunca refrescar a própria request de refresh.
        if (failedRequest.isRefreshRequest()) return null

        // Guard 2: sem refresh token salvo, não há o que renovar.
        val refreshToken = sessionManager.getRefreshToken() ?: return null

        var shouldTag = false

        val refreshed = runBlocking {
            refreshMutex.withLock {
                when {
                    // A request marcada voltou a receber 401 após o refresh:
                    // sessão morta, logout.
                    failedRequest.tag(RefreshRetryTag::class.java) != null -> {
                        sessionManager.clearSession()
                        false
                    }

                    // Outro thread já renovou o token: apenas reencaminha.
                    sessionManager.getToken() != failedRequest.bearerToken() -> true

                    // Refresh acabou de falhar para este mesmo token: evita
                    // N tentativas sequenciais no mesmo burst.
                    System.currentTimeMillis() - lastRefreshAttemptFailedAt < REFRESH_FAILURE_COOLDOWN_MS -> false

                    else -> {
                        val success = refreshSession(refreshToken)
                        if (success) {
                            shouldTag = true
                        }
                        success
                    }
                }
            }
        }

        if (!refreshed) return null

        val currentToken = sessionManager.getToken() ?: return null

        // Request reconstruída com o novo Bearer: application interceptors
        // não rodam de novo no retry disparado pelo Authenticator.
        return failedRequest.newBuilder()
            .header("Authorization", "Bearer $currentToken")
            .apply {
                if (shouldTag) {
                    tag(RefreshRetryTag::class.java, RefreshRetryTag)
                }
            }
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
            lastRefreshAttemptFailedAt = 0L
            sessionManager.saveSession(tokens.toDomain())
            true
        } catch (e: HttpException) {
            lastRefreshAttemptFailedAt = System.currentTimeMillis()
            if (e.code() == 401) {
                sessionManager.clearSession()
            }
            false
        } catch (e: IOException) {
            lastRefreshAttemptFailedAt = System.currentTimeMillis()
            false
        } catch (e: Exception) {
            lastRefreshAttemptFailedAt = System.currentTimeMillis()
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