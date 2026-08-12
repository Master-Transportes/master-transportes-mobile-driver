package com.master.transportes.driver.core.network

import com.master.transportes.driver.core.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            sessionManager.getToken()?.let {
                addHeader("Authorization", "Bearer $it")
            }
        }.build()

        return chain.proceed(request)
    }
}
