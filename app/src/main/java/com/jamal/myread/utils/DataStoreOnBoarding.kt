package com.jamal.myread.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.jamal.myread.dataStoreOnBoarding
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataStoreOnBoarding @Inject constructor() {

    suspend fun saveOnBoardingPreference(
        context: Context,
        key: Preferences.Key<Boolean>,
        value: Boolean
    ) {
        context.dataStoreOnBoarding.edit { onBoardingPreference ->
            onBoardingPreference[key] = value
        }
    }

    suspend fun readOnBoardingPreference(
        context: Context,
        key: Preferences.Key<Boolean>
    ): Boolean {
        val preferences = context.dataStoreOnBoarding.data.first()
        return preferences[key] ?: false
    }

}