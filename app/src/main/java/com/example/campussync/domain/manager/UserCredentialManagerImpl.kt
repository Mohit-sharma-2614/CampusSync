package com.example.campussync.domain.manager

import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.campussync.data.manager.StorageManager
import com.example.campussync.data.manager.UserCredentialManager
import kotlinx.coroutines.flow.first

class UserCredentialManagerImpl(
    private val storageManager: StorageManager
): UserCredentialManager {
    private val userIdKey = stringPreferencesKey("userId")

    override suspend fun saveUserId(userId: String) {
        storageManager.saveData(userId, userIdKey)
        Log.d("UserCredentialManager", "saveUserId: $userId")
    }

    override suspend fun getUserId(): String {
        val userId = storageManager.readPrefs(userIdKey).first()
        Log.d("UserCredentialManager", "getUserId: $userId")
        return userId
    }

    override suspend fun clearUserId() {
        storageManager.clearPrefs(userIdKey)
        Log.d("UserCredentialManager", "clearUserId: cleared")
    }
}
