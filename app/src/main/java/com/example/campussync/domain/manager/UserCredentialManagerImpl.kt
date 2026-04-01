package com.example.campussync.domain.manager

import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.campussync.data.manager.StorageManager
import com.example.campussync.data.manager.UserCredentialManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserCredentialManagerImpl(
    private val storageManager: StorageManager
): UserCredentialManager {
    private val userIdKey = stringPreferencesKey("userId")

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun saveUserId(userId: String) {
        coroutineScope.launch {
            storageManager.saveData(userId, userIdKey)
        }
    }

    override fun getUserId(): String {
        return runBlocking(Dispatchers.IO) {
            storageManager.readPrefs(userIdKey).first()
        }
    }

    override fun clearUserId() {
        coroutineScope.launch {
            storageManager.clearPrefs(userIdKey)
        }
    }
}