package com.example.bigjourney

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Ορισμός του DataStore στο application context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object UserPreferencesManager {

    // Ορισμός του κλειδιού για την αποθήκευση του dark mode
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    // Συνάρτηση για την αποθήκευση της προτίμησης dark mode
    suspend fun saveDarkModePreference(context: Context, isDarkModeEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkModeEnabled
        }
    }

    // Ροή (Flow) για την ανάκτηση της κατάστασης του dark mode
    fun darkModePreference(context: Context): Flow<Boolean> {
        return context.dataStore.data
            .map { preferences ->
                preferences[DARK_MODE_KEY] ?: false
            }
    }
}














