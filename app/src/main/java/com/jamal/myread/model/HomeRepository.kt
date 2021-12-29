package com.jamal.myread.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(private val screenReaderService: ScreenReaderService) {

    fun startService(activity: Activity, context: Context) {
        activity.startService(Intent(context, screenReaderService::class.java))
    }
}