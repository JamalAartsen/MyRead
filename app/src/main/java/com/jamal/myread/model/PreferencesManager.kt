package com.jamal.myread.model
//
//import android.content.Context
//import androidx.datastore.core.DataStore
//import androidx.datastore.dataStore
//import androidx.datastore.preferences.core.*
//import androidx.datastore.preferences.preferencesDataStore
//import dagger.hilt.android.qualifiers.ApplicationContext
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.map
//import java.io.IOException
//import javax.inject.Inject
//import javax.inject.Singleton
//
//private const val TAG = "PreferencesManager"
//
////data class PreferencesVoice(val pitch: Float, val speed: Float)
//
//@Singleton
//class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {
//
//    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
//
//    val preferencesFlow = context.dataStore.data
//        .catch { exception ->
//            if (exception is IOException) {
//                emit(emptyPreferences())
//            } else {
//                throw exception
//            }
//        }
//        .map { preferences ->
//            val pitch = preferences[PreferencesKeys.PITCH] ?: 1f
//            val speed = preferences[PreferencesKeys.SPEED] ?: 1f
//
//            PreferencesVoice(pitch, speed)
//        }
//
//    suspend fun updatePitch(pitch: Float, context: Context) {
//        context.dataStore.edit { preferences ->
//            preferences[PreferencesKeys.PITCH] = pitch
//        }
//    }
//
//    suspend fun updateSpeed(speed: Float, context: Context) {
//        context.dataStore.edit { preferences ->
//            preferences[PreferencesKeys.SPEED] = speed
//        }
//    }
//
//    private object PreferencesKeys {
//        val PITCH = floatPreferencesKey("pitch")
//        val SPEED = floatPreferencesKey("speed")
//    }
//}