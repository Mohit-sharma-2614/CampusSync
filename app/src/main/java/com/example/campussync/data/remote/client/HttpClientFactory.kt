package com.example.campussync.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import okhttp3.internal.connection.ConnectInterceptor.intercept

object HttpClientFactory {
    fun create(
        baseUrl: String,
        tokenProvider: (() -> String?)? = null,
        onRefreshToken: (suspend () -> String?)? = null
    ): HttpClient {
        return HttpClient {
            expectSuccess = false
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            install(DefaultRequest) {
                url(baseUrl)
                header("Content-Type","application/json")
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 15_000
            }

            tokenProvider?.let { provider ->
                install(Auth) {
                    bearer {
                        loadTokens {
                            val token = provider()
                            if (token != null) {
                                BearerTokens(
                                    accessToken = token,
                                    refreshToken = token
                                )
                            } else { null }
                        }

                        refreshTokens {
                            val newToken = onRefreshToken?.invoke()

                            if(newToken != null) {
                                BearerTokens(
                                    accessToken = newToken,
                                    refreshToken = newToken
                                )
                            } else {
                                null
                            }
                        }

                        // Don't send token for login endpoint
                        sendWithoutRequest { request ->
                            !request.url.encodedPath.contains("/login")
                        }
                    }
                }
            }
        }
    }
}