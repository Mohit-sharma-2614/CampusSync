package com.example.campussync.data.remote.client

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()

        return authenticatedRequest.let {
            chain.proceed(it.build())
        }
    }
}