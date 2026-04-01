package com.example.campussync.domain.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.campussync.data.manager.StorageManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_data")

/**
 * Implementation of the [StorageManager] interface
 * @param context The application context
 */
class StorageManagerImpl(
    private val context: Context
): StorageManager {
    /**
     * Reads the auth token from the datastore
     * @param fileName The key to read the auth token under
     * @return `Flow<String>` The auth token
     */
    override suspend fun readPrefs(fileName: Preferences.Key<String>): Flow<String> =
        try {
            context.dataStore.data.map { preferences ->
                preferences[fileName] ?: ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emptyFlow()
        }

    /**
     * Saves the auth token to the datastore
     * @param data The auth token to save
     * @param fileName The key to save the auth token under
     */
    override suspend fun saveData(
        data: String,
        fileName: Preferences.Key<String>
    ) {
        try {
            context.dataStore.updateData {
                it.toMutablePreferences().also { preferences ->
                    preferences[fileName] = data
                }
            }
        } catch (e: IOException){
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Clears the auth token from the datastore
     * @param fileName The key to clear the auth token under
     * @return `Unit`
     */
    override suspend fun clearPrefs(
        fileName: Preferences.Key<String>
    ) {
        try {
            context.dataStore.edit {
                it.remove(fileName)
            }
        } catch (e: IOException){
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}