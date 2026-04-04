package com.example.campussync.data.manager

interface UserCredentialManager {
    fun saveUserId(userId: String)
    fun getUserId(): String

    fun clearUserId()
}