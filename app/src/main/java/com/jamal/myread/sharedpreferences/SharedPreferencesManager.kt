package com.jamal.myread.sharedpreferences

import android.app.Activity
import android.content.Context

private const val APP_KILLED = "App Killed"

object SharedPreferencesManager {

    fun saveServiceRunningAppKilled(activity: Activity, key: String, value: Boolean) {
        activity.getSharedPreferences(APP_KILLED, Context.MODE_PRIVATE).edit()
            .putBoolean(key, value).apply()
    }

    fun getServiceRunningAppKilled(activity: Activity, key: String): Boolean {
        return activity.getSharedPreferences(APP_KILLED, Context.MODE_PRIVATE)
            .getBoolean(key, false)
    }

}