package com.jamal.myread.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor() {
    fun startService(activity: Activity, context: Context, resultCode: Int, data: Intent) {
        activity.startService(
            ScreenReaderService.getStartIntent(
                context,
                resultCode,
                data
            )
        )
    }

    fun stopScreenShot(activity: Activity, context: Context) {
        activity.startService(ScreenReaderService.getStopIntent(context))
    }
}