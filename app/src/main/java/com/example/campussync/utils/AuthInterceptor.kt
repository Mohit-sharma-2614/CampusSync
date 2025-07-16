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
        val isTokenRequest = request.url.encodedPath.contains("api/auth/validate")

        Log.d("AuthInterceptor", "Intercepting request to: ${request.url}")
        Log.d("AuthInterceptor", "Is login request: $isLoginRequest")
        Log.d("AuthInterceptor", "Is token request: $isTokenRequest")
        Log.d("AuthInterceptor", "Current token from TokenManager: ${if (token.isNullOrEmpty()) "NULL/EMPTY" else "PRESENT"}")

        if (!token.isNullOrEmpty() && !isLoginRequest && !isTokenRequest) {
            Log.d("AuthInterceptor", "Attempting to add Authorization header with token: |$token|")
            Log.d("AuthInterceptor", "|${("Bearer $token").trim()}|")
            // --- AND ALSO TRIM HERE FOR DOUBLE CHECKING ---
            requestBuilder.addHeader("Authorization", ("Bearer $token").trim()) // Defensive trimming
            Log.d("AuthInterceptor", "Adding Authorization header.")
        } else if (token.isNullOrEmpty() && !isLoginRequest) {
            Log.w("AuthInterceptor", "No token available for authenticated request: ${request.url}")
        } else if (isLoginRequest) {
            Log.d("AuthInterceptor", "Skipping Authorization header for login request.")
        }

        return chain.proceed(requestBuilder.build())
    }
}