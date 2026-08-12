package com.master.transportes.driver.core.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val SESSION_ID_KEY = stringPreferencesKey("session_id")
        private val EXPIRES_IN_KEY = longPreferencesKey("expires_in")
    }

    private val _token = MutableStateFlow<String?>(null)

    @Volatile
    private var refreshToken: String? = null

    @Volatile
    private var sessionId: String? = null

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val prefs = dataStore.data.first()
            val token = prefs[TOKEN_KEY]
            _token.value = token
            refreshToken = prefs[REFRESH_TOKEN_KEY]
            sessionId = prefs[SESSION_ID_KEY]
            _isLoggedIn.value = token != null
        }
    }

    fun getToken(): String? = _token.value

    fun getRefreshToken(): String? = refreshToken

    fun getSessionId(): String? = sessionId

    suspend fun saveSession(session: Session) {
        _token.value = session.token
        refreshToken = session.refreshToken
        sessionId = session.sessionId
        _isLoggedIn.value = true
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = session.token
            prefs[REFRESH_TOKEN_KEY] = session.refreshToken
            prefs[SESSION_ID_KEY] = session.sessionId
            prefs[EXPIRES_IN_KEY] = session.expiresIn
        }
    }

    suspend fun clearSession() {
        _token.value = null
        refreshToken = null
        sessionId = null
        _isLoggedIn.value = false
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
            prefs.remove(SESSION_ID_KEY)
            prefs.remove(EXPIRES_IN_KEY)
        }
    }
}
