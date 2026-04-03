package com.example.campussync.di


import com.example.campussync.data.manager.TokenManager
import com.example.campussync.data.remote.client.HttpClientFactory
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenInputDto
import com.example.campussync.domain.manager.TokenManagerImpl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
        "http://10.0.2.2:8080" // can run on emulators
        // "http://192.168.1.37:8080" // only can run on usb debugging physical device
    }

    // Public client no auth
    single(PUBLIC_CLIENT) {
        HttpClientFactory.create(baseUrl = get(BASE_URL))
    }

    // Auth client
    single(AUTH_CLIENT) {
        val tokenManager = get<TokenManager>()

        HttpClientFactory.create(
            baseUrl = get(BASE_URL),
            tokenProvider = { tokenManager.getToken() },
            onRefreshToken = {
                try {
                    val publicClient = get<HttpClient>(PUBLIC_CLIENT)

                    val response = publicClient.post("/api/auth/refresh-token") {
                        setBody(mapOf("refreshToken" to tokenManager.getRefreshToken()))
                    }

                    val newToken = response.body<RefreshTokenInputDto>().refreshToken
                    tokenManager.saveToken(newToken)

                    newToken
                } catch (e: Exception) {
                    null
                }
            }
        )
    }
}
