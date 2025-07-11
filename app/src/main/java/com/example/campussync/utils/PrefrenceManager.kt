package com.example.campussync.utils

// PreferenceManager.kt
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// PreferenceManager.kt
object PreferenceKeys {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val USER_ID = stringPreferencesKey("user_id") // Changed to String for flexibility (UUID, etc.)
    val IS_TEACHER = booleanPreferencesKey("is_teacher")
}

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // ✅ Access the dataStore via context extension
    private val dataStore = context.dataStore

    val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.IS_LOGGED_IN] ?: false
        }

    val userId: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.USER_ID]
        }

    val isTeacher: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.IS_TEACHER] ?: false
        }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_LOGGED_IN] = loggedIn
        }
    }

    suspend fun setUserInfo(userId: String, isTeacher: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_ID] = userId
            preferences[PreferenceKeys.IS_TEACHER] = isTeacher
            preferences[PreferenceKeys.IS_LOGGED_IN] = true // Automatically log in when setting user info
        }
    }

    suspend fun clearUserInfo() {
        dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.USER_ID)
            preferences.remove(PreferenceKeys.IS_TEACHER)
            preferences[PreferenceKeys.IS_LOGGED_IN] = false // Log out when clearing user info
        }
    }
}