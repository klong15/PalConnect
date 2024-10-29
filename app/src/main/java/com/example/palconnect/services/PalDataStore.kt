package com.example.palconnect.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val IP_PREFERENCE = stringPreferencesKey("ip")
val PASS_PREFERENCE = stringPreferencesKey("password")
val DYN_COLOR_PREFERENCE = booleanPreferencesKey("dynamicColor")

class PalDataStore(val context: Context) {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")



    val ipFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[IP_PREFERENCE] ?: ""
    }

    val passwordFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PASS_PREFERENCE] ?: ""
    }

    val dynamicColorFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DYN_COLOR_PREFERENCE] == true
    }

    suspend fun saveLoginConfig(ip: String, password: String) {
        context.dataStore.edit { settings ->
            settings[IP_PREFERENCE] = ip
            settings[PASS_PREFERENCE] = password
        }
    }

    suspend fun saveDynamicColorPreference(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[DYN_COLOR_PREFERENCE] = value
        }
    }
}