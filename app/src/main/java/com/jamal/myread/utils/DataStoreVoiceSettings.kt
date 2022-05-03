package com.jamal.myread.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import com.jamal.myread.dataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataStoreVoiceSettings @Inject constructor() {

    suspend fun saveVoiceSettings(
        context: Context,
        key: Preferences.Key<Float>,
        value: Float
    ) {
        context.dataStore.edit { voiceSettings ->
            voiceSettings[key] = value
        }
    }

    suspend fun readVoiceSettings(
        context: Context,
        key: Preferences.Key<Float>
    ): Float {
        val preferences = context.dataStore.data.first()
        return preferences[key] ?: 1f
    }
}

object PreferencesKeys {
    val PITCH = floatPreferencesKey("pitch")
    val SPEED = floatPreferencesKey("speed")
}
