package com.example.campussync.data.manager

interface TokenManager {
    fun saveRefreshToken(token: String)
    fun getRefreshToken(): String?
    fun clearRefreshToken()
    fun saveToken(token: String)
    fun getToken(): String
    fun clearToken()
}

