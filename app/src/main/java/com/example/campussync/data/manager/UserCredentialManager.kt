package com.example.campussync.data.manager

interface UserCredentialManager {
    suspend fun saveUserId(userId: String)
    suspend fun getUserId(): String
    suspend fun clearUserId()
}
