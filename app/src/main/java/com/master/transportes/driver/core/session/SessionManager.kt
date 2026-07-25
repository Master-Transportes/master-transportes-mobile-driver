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
        private val EXPIRES_IN_KEY = longPreferencesKey("expires_in")
    }

    private val _token = MutableStateFlow<String?>(null)

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val token = dataStore.data.first()[TOKEN_KEY]
            _token.value = token
            _isLoggedIn.value = token != null
        }
    }

    fun getToken(): String? = _token.value

    suspend fun saveSession(session: Session) {
        _token.value = session.token
        _isLoggedIn.value = true
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = session.token
            prefs[EXPIRES_IN_KEY] = session.expiresIn
        }
    }

    suspend fun clearSession() {
        _token.value = null
        _isLoggedIn.value = false
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(EXPIRES_IN_KEY)
        }
    }
}
