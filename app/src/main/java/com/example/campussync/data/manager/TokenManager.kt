package com.example.campussync.data.manager

interface TokenManager {
    suspend fun saveRefreshToken(token: String)
    suspend fun getRefreshToken(): String?
    suspend fun clearRefreshToken()
    suspend fun saveToken(token: String)
    suspend fun getToken(): String
    suspend fun clearToken()
}
