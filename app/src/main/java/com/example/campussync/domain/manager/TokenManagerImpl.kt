package com.example.campussync.domain.manager

import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.campussync.data.manager.StorageManager
import com.example.campussync.data.manager.TokenManager
import kotlinx.coroutines.flow.first

class TokenManagerImpl(
    private val storageManager: StorageManager
): TokenManager {
    private val AUTH_TOKEN = stringPreferencesKey("auth_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")

    override suspend fun saveRefreshToken(token: String) {
        storageManager.saveData(token, REFRESH_TOKEN)
        Log.d("TokenManager", "saveRefreshToken: saved")
    }

    override suspend fun getRefreshToken(): String? {
        val refreshToken = storageManager.readPrefs(REFRESH_TOKEN).first()
        return refreshToken.ifBlank { null }
    }

    override suspend fun clearRefreshToken() {
        storageManager.clearPrefs(REFRESH_TOKEN)
        Log.d("TokenManager", "clearRefreshToken: cleared")
    }

    override suspend fun saveToken(token: String) {
        storageManager.saveData(token, AUTH_TOKEN)
        Log.d("TokenManager", "saveToken: saved")
    }

    override suspend fun getToken(): String {
        val token = storageManager.readPrefs(AUTH_TOKEN).first()
        Log.d("TokenManager", "getToken: $token")
        return token
    }

    override suspend fun clearToken() {
        storageManager.clearPrefs(AUTH_TOKEN)
        Log.d("TokenManager", "clearToken: cleared")
    }
}
