package com.jamal.myread.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.datastore.preferences.core.*
import androidx.lifecycle.ViewModel
import com.jamal.myread.dataStore
import com.jamal.myread.model.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

data class PreferencesVoice(val pitch: Float, val speed: Float)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val preferencesFlow: Flow<PreferencesVoice> = context.dataStore.data
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
        preferencesFlow.collect { flow ->
            repository.startService(activity, context, resultCode, data, flow.pitch, flow.speed)
        }
    }

    /**
     * Edits values inside datatsore of de specific given key.
     *
     * @param context
     * @param pitch
     * @param speed
     *
     * @author Jamal Aartsen
     */
    suspend fun updatePreferencesVoice(context: Context, pitch: Float?, speed: Float?) {
        context.dataStore.edit { preferences ->
            checkIfValueIsBelowCertainNumber(pitch, preferences, PreferencesKeys.SPEED)
            checkIfValueIsBelowCertainNumber(speed, preferences, PreferencesKeys.PITCH)
        }
    }

    /**
     * Check if the given value is below 0.1f and set given value to datastore.
     *
     * @param value given float value (pitch or speed)
     * @param preferences
     * @param preferencesKeys
     *
     * @author Jamal Aartsen
     */
    private fun checkIfValueIsBelowCertainNumber(
        value: Float?,
        preferences: MutablePreferences,
        preferencesKeys: Preferences.Key<Float>
    ) {
        if (value != null) {
            if (value < 0.1f) preferences[preferencesKeys] = 0.1f
            else preferences[preferencesKeys] = value
        } else {
            preferences[preferencesKeys] = 1f
        }
    }

    /**
     *  Checks if phone SDK is over version M (23), so that user can give permission to draw
     *  on top of other apps.
     *
     *  @param context
     *
     *  @author Jamal Aartsen
     */
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