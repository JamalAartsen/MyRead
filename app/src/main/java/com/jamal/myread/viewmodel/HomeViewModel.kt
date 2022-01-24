package com.jamal.myread.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import com.jamal.myread.model.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class PreferencesVoice(val pitch: Float, val speed: Float)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val TAG = "HomeViewModel"

     fun startService(activity: Activity, context: Context, resultCode: Int, data: Intent, pitch: Float?, speed: Float?) {

        val pitchValue = pitch ?: 1f

        val speedValue = speed ?: 1f

        repository.startService(activity, context, resultCode, data, pitchValue, speedValue)
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
}