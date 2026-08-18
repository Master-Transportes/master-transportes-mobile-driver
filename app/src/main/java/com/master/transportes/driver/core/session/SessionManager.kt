package com.master.transportes.driver.core.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.master.transportes.driver.core.error.AppError
import com.master.transportes.driver.di.ApplicationScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val SESSION_ID_KEY = stringPreferencesKey("session_id")
        private val EXPIRES_IN_KEY = longPreferencesKey("expires_in")

        // Códigos de erro que indicam sessão inválida/morta.
        private val SESSION_DEAD_CODES = setOf("not_found", "unauthenticated")
    }

    private val _token = MutableStateFlow<String?>(null)

    @Volatile
    private var refreshToken: String? = null

    @Volatile
    private var sessionId: String? = null

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading);

    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private val _logoutEvents = Channel<Unit>(Channel.BUFFERED)
    val logoutEvents: Flow<Unit> = _logoutEvents.receiveAsFlow()

    init {
        applicationScope.launch {
            try{
                val prefs = dataStore.data.first()
                val token = prefs[TOKEN_KEY]
                val savedRefreshToken = prefs[REFRESH_TOKEN_KEY]
                val savedSessionId = prefs[SESSION_ID_KEY]
                val savedExpiresIn = prefs[EXPIRES_IN_KEY] ?: 0L

                _token.value = token
                refreshToken = savedRefreshToken
                sessionId = savedSessionId

                _sessionState.value =
                    if (token != null && savedRefreshToken != null && savedSessionId != null) {
                        SessionState.Authenticated(
                            Session(
                                token = token,
                                refreshToken = savedRefreshToken,
                                sessionId = savedSessionId,
                                expiresIn = savedExpiresIn
                            )
                        )
                    } else {
                        SessionState.Unauthenticated
                    }

            } catch (e: CancellationException) {
                throw e
            }
            catch (e: Exception) {
                _sessionState.value = SessionState.Unauthenticated
            }
        }
    }

    fun getToken(): String? = _token.value

    fun getRefreshToken(): String? = refreshToken

    fun getSessionId(): String? = sessionId

    suspend fun saveSession(session: Session) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = session.token
            prefs[REFRESH_TOKEN_KEY] = session.refreshToken
            prefs[SESSION_ID_KEY] = session.sessionId
            prefs[EXPIRES_IN_KEY] = session.expiresIn
        }
        _token.value = session.token
        refreshToken = session.refreshToken
        sessionId = session.sessionId
        _sessionState.value = SessionState.Authenticated(session)
    }

    suspend fun clearSession() {
        val wasAuthenticated = _sessionState.value is SessionState.Authenticated
        try {
            dataStore.edit { prefs ->
                prefs.remove(TOKEN_KEY)
                prefs.remove(REFRESH_TOKEN_KEY)
                prefs.remove(SESSION_ID_KEY)
                prefs.remove(EXPIRES_IN_KEY)
            }
            _token.value = null
            refreshToken = null
            sessionId = null
            _sessionState.value = SessionState.Unauthenticated

            if (wasAuthenticated) {
                _logoutEvents.send(Unit)
            }
        } catch (e: IOException) {
            // Mantém o estado atual para não limpar pela metade.
        }
    }

    /**
     * Verifica se o erro indica que a sessão não é mais válida
     * e, se for o caso, limpa a sessão.
     *
     * @return true se a sessão foi limpa, false caso contrário.
     */
    suspend fun handleSessionExpired(error: AppError): Boolean {
        if (error is AppError.Api && error.code in SESSION_DEAD_CODES && _sessionState.value is SessionState.Authenticated) {
            clearSession()
            return true
        }
        return false
    }
}
