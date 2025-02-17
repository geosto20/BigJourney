package com.example.bigjourney
/*
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserDataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    suspend fun saveUserData(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = value
        }
    }

    fun loadUserData(key: String): Flow<String?> {
        val dataStoreKey = stringPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[dataStoreKey]
        }
    }

    suspend fun saveBooleanData(key: String, value: Boolean) {
        val dataStoreKey = booleanPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = value
        }
    }

    fun loadBooleanData(key: String): Flow<Boolean?> {
        val dataStoreKey = booleanPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[dataStoreKey]
        }
    }
}
*/







