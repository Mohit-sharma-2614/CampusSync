package com.example.campussync.domain.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.campussync.data.manager.StorageManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_data")

class StorageManagerImpl(
    private val context: Context
): StorageManager {

    override suspend fun readPrefs(fileName: Preferences.Key<String>): Flow<String> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[fileName] ?: ""
            }

    override suspend fun saveData(
        data: String,
        fileName: Preferences.Key<String>
    ) {
        try {
            context.dataStore.edit { preferences ->
                preferences[fileName] = data
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun clearPrefs(
        fileName: Preferences.Key<String>
    ) {
        try {
            context.dataStore.edit { preferences ->
                preferences.remove(fileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
