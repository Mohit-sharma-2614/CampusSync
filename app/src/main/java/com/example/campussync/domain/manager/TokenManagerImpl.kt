package com.example.campussync.domain.manager

import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.campussync.data.manager.StorageManager
import com.example.campussync.data.manager.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
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
        }
    }

    override fun getRefreshToken(): String {
        return runBlocking(Dispatchers.IO) {
            storageManager.readPrefs(REFRESH_TOKEN).first()
        }
    }

    override fun clearRefreshToken() {
        coroutineScope.launch {
            storageManager.clearPrefs(REFRESH_TOKEN)
        }
    }

    override fun saveToken(token: String) {
        coroutineScope.launch {
            storageManager.saveData(
                token,
                AUTH_TOKEN
            )
        }
    }

    override fun getToken(): String {
        return runBlocking(Dispatchers.IO) {
            storageManager.readPrefs(AUTH_TOKEN).first()
        }
    }

    override fun clearToken() {
        coroutineScope.launch {
            storageManager.clearPrefs(AUTH_TOKEN)
        }
    }

}