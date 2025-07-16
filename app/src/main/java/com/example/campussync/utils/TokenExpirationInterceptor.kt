package com.example.campussync.utils

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

// Define a sealed class for auth events
sealed class AuthEvent {
    object Unauthorized : AuthEvent()
}

@Singleton
class TokenExpirationInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    // SharedFlow to emit auth-related events
    private val _authEvents = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val authEvents = _authEvents.asSharedFlow()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 401) {
            Log.e("TokenExpiration", "Received 401 Unauthorized for URL: ${request.url}")
            Log.e("TokenExpiration", "Clearing token due to 401.")
            tokenManager.clearToken()
            // Emit an Unauthorized event to notify the UI
            _authEvents.tryEmit(AuthEvent.Unauthorized)
        }
        return response
    }
}