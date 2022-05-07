package com.jamal.myread

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "voice_Settings")
val Context.dataStoreOnBoarding: DataStore<Preferences> by preferencesDataStore(name = "on_boarding")