package com.jamal.myread.sharedpreferences

import android.app.Activity
import android.content.Context
import com.jamal.myread.sharedpreferences.SharedPreferencesKeys.ON_BOARDING_PREFERENCES

object SharedPreferenceOnBoarding {

    fun savePreferences(activity: Activity, key: String, value: Boolean) {
        activity.getSharedPreferences(ON_BOARDING_PREFERENCES, Context.MODE_PRIVATE).edit().apply {
            putBoolean(key, value)
        }.apply()
    }

    fun getPreferences(activity: Activity, key: String): Boolean {
        return activity.getSharedPreferences(ON_BOARDING_PREFERENCES, Context.MODE_PRIVATE)
            .getBoolean(key, false)
    }
}