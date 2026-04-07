package com.example.campussync.data.remote.client

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    // Note how we update the lambdas to return `BearerTokens?` nullable values
    fun create(
        baseUrl: String,
        tokenProvider: (suspend () -> BearerTokens?)? = null,
        onRefreshToken: (suspend () -> BearerTokens?)? = null
    ): HttpClient {
        return HttpClient {
            expectSuccess = false

            // Standard plugins
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            install(DefaultRequest) {
                url(baseUrl)
                header("Content-Type", "application/json")
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 15_000
            }
            // Only install Auth if we provided a tokenProvider
            tokenProvider?.let { provider ->
                install(Auth) {
                    bearer {
                        loadTokens {
                            val tokens = provider()
                            Log.i("HttpClientFactory", "loadTokens: ${tokens?.accessToken}")
                            tokens // Valid tokens or null
                        }
                        refreshTokens {
                            Log.i("HttpClientFactory", "refreshTokens triggered")
                            // We can just rely on the onRefreshToken block doing validation
                            // and returning null if it failed.
                            onRefreshToken?.invoke()
                        }
                        sendWithoutRequest { request ->
                            // By default Ktor will wait for a 401 Unauthorized before attaching tokens.
                            // To preemptively attach the token, this must return true.
                            // We want to attach it to all routes EXCEPT login/register:
                            val path = request.url.encodedPath
                            !path.contains("/login") && !path.contains("/register")
                        }
                    }
                }
            }
        }
    }
}