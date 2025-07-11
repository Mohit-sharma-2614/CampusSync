package com.example.campussync.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        val isLoginRequest = request.url.encodedPath.contains("login")

        Log.d("AuthInterceptor", "Intercepting request to: ${request.url}")
        Log.d("AuthInterceptor", "Is login request: $isLoginRequest")
        Log.d("AuthInterceptor", "Current token from TokenManager: ${if (token.isNullOrEmpty()) "NULL/EMPTY" else "PRESENT"}")

        if (!token.isNullOrEmpty() && !isLoginRequest) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Log.d("AuthInterceptor", "Adding Authorization header.")
        } else if (token.isNullOrEmpty() && !isLoginRequest) {
            Log.w("AuthInterceptor", "No token available for authenticated request: ${request.url}")
        } else if (isLoginRequest) {
            Log.d("AuthInterceptor", "Skipping Authorization header for login request.")
        }

        return chain.proceed(requestBuilder.build())
    }
}

// TokenExpirationInterceptor.kt
class TokenExpirationInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 401) {
            Log.e("TokenExpiration", "Received 401 Unauthorized for URL: ${request.url}")
            Log.e("TokenExpiration", "Clearing token due to 401.")
            tokenManager.clearToken()
            // In a real app, you might also want to notify the UI to navigate to login.
            // This could be done via a shared Flow or Channel.
        }
        return response
    }
}