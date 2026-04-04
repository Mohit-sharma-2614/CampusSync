package com.example.campussync.domain.manager

import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.campussync.data.manager.StorageManager
import com.example.campussync.data.manager.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TokenManagerImpl(
    private val storageManager: StorageManager
): TokenManager {
    private val AUTH_TOKEN = stringPreferencesKey("auth_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun saveRefreshToken(token: String) {
        coroutineScope.launch {
            storageManager.saveData(token, REFRESH_TOKEN)
            Log.d("TokenManager", "saveRefreshToken: $token")
        }
    }

    override fun getRefreshToken(): String {
        return runBlocking(Dispatchers.IO) {
            val refreshToken = storageManager.readPrefs(REFRESH_TOKEN).first()
            Log.d("TokenManager", "getRefreshToken: $refreshToken")
            refreshToken
        }
    }

    override fun clearRefreshToken() {
        coroutineScope.launch {
            storageManager.clearPrefs(REFRESH_TOKEN)
            Log.d("TokenManager", "clearRefreshToken: cleared")
        }
    }

    override fun saveToken(token: String) {
        coroutineScope.launch {
            storageManager.saveData(
                token,
                AUTH_TOKEN
            )
            Log.d("TokenManager", "saveToken: $token")
        }
    }

    override fun getToken(): String {
        return runBlocking(Dispatchers.IO) {
            val token = storageManager.readPrefs(AUTH_TOKEN).first()
            Log.d("TokenManager", "getToken: $token")
            token
        }
    }

    override fun clearToken() {
        coroutineScope.launch {
            storageManager.clearPrefs(AUTH_TOKEN)
            Log.d("TokenManager", "clearToken: cleared")
        }
    }

}