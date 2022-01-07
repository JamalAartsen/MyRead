package com.jamal.myread.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.lifecycle.ViewModel
import com.jamal.myread.dataStore
import com.jamal.myread.model.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

data class PreferencesVoice(val pitch: Float, val speed: Float)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    val preferencesFlow: Flow<PreferencesVoice> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val pitch = preferences[PreferencesKeys.PITCH] ?: 1f
            val speed = preferences[PreferencesKeys.SPEED] ?: 1f
            PreferencesVoice(pitch, speed)
        }


    suspend fun startService(activity: Activity, context: Context, resultCode: Int, data: Intent) {
        preferencesFlow.collect { pitchFlow ->
            repository.startService(activity, context, resultCode, data, PreferencesVoice(pitchFlow.pitch, pitchFlow.speed))
        }
    }

    suspend fun updatePreferencesVoice(context: Context, pitch: Float?, speed: Float?) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SPEED] = speed ?: 1f
            preferences[PreferencesKeys.PITCH] = pitch ?: 1f
        }
    }

    fun checkOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else return true
    }

    private object PreferencesKeys {
        val PITCH = floatPreferencesKey("pitch")
        val SPEED = floatPreferencesKey("speed")
    }
}