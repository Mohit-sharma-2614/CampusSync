package com.example.campussync.di


import android.util.Log
import com.example.campussync.data.manager.TokenManager
import com.example.campussync.data.remote.client.HttpClientFactory
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenResponseDto
import com.example.campussync.domain.manager.TokenManagerImpl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.koin.core.qualifier.named
import org.koin.dsl.module

val PUBLIC_CLIENT = named("publicClient")
val AUTH_CLIENT = named("authClient")
val BASE_URL = named("baseUrl")

val networkModule = module {

    single<TokenManager> { TokenManagerImpl(get()) }

    single(BASE_URL) {
        // "http://10.0.2.2:8080" // can run on emulators
        "http://172.25.134.243:8080" // only can run on usb debugging physical device
    }

    // Public client no auth
    single(PUBLIC_CLIENT) {
        HttpClientFactory.create(baseUrl = get(BASE_URL))
    }

    // Auth client
    single(AUTH_CLIENT) {
        val tokenManager = get<TokenManager>()
        val publicClient = get<HttpClient>(PUBLIC_CLIENT) // Safe to inject here since it's a Singleton

        HttpClientFactory.create(
            baseUrl = get(BASE_URL),
            tokenProvider = {
                val token = tokenManager.getToken()
                val refreshToken = tokenManager.getRefreshToken()

                // If there's no auth token, return null so Ktor knows we are unauthenticated
                if (token.isNotBlank()) {
                    BearerTokens(token, refreshToken)
                } else {
                    null
                }
            },
            onRefreshToken = {
                try {
                    val currentRefreshToken = tokenManager.getRefreshToken()
                    if (currentRefreshToken.isNullOrBlank()) {
                        return@create null // Cannot refresh if we don't have a refresh token
                    }

                    // Use the public client to avoid getting trapped in an interceptor loop
                    val response = publicClient.post("/api/auth/refreshtoken") {
                        setBody(mapOf("refreshToken" to currentRefreshToken))
                    }

                    // Ensure the refresh actually succeeded (2xx Status Code)
                    if (response.status.value in 200..299) {
                        val body = response.body<RefreshTokenResponseDto>()

                        tokenManager.saveToken(body.accessToken)
                        tokenManager.saveRefreshToken(body.refreshToken)

                        BearerTokens(
                            body.accessToken,
                            body.refreshToken
                        )
                    } else {
                        // If refresh fails (e.g., token expired), you usually want to clear tokens
                        // and eventually force the user to login again
                        Log.e("HttpClientFactory", "Refresh failed with status: ${response.status}")
//                        tokenManager.clearToken()
//                        tokenManager.clearRefreshToken()
                        null
                    }
                } catch (e: Exception) {
                    Log.e("HttpClientFactory", "onRefreshToken exception: ${e.message}")
                    null
                }
            }
        )
    }
}
