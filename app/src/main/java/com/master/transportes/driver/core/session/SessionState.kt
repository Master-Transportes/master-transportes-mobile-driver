package com.master.transportes.driver.core.session

sealed interface SessionState {
    data object Loading: SessionState
    data class Authenticated(val session: Session): SessionState
    data object Unauthenticated: SessionState
}