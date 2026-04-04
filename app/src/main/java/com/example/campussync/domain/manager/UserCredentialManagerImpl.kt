package com.example.campussync.domain.manager

import android.util.Log
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
            Log.d("UserCredentialManager", "saveUserId: $userId")
        }
    }

    override fun getUserId(): String {
        return runBlocking(Dispatchers.IO) {
            val userId = storageManager.readPrefs(userIdKey).first()
            Log.d("UserCredentialManager", "getUserId: $userId")
            userId
        }
    }

    override fun clearUserId() {
        coroutineScope.launch {
            storageManager.clearPrefs(userIdKey)
            Log.d("UserCredentialManager", "clearUserId: cleared")
        }
    }
}