package com.example.campussync.data.manager

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface StorageManager {
    suspend fun readPrefs(
        fileName: Preferences.Key<String>
    ): Flow<String>
    suspend fun clearPrefs(
        fileName: Preferences.Key<String>
    )
    suspend fun saveData(
        authToken: String,
        fileName: Preferences.Key<String>
    )
}