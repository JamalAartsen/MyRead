package com.jamal.myread.utils

import android.app.Activity
import android.content.Context

private const val ON_BOARDING_PREFERENCES = "OnBoardingPreferences"

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