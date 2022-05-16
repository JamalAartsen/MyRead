package com.jamal.myread.viewmodel

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import com.jamal.myread.model.HomeRepository
import com.jamal.myread.model.ScreenReaderService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val resultChannel = Channel<Result>()
    val result = resultChannel.receiveAsFlow()

    private fun startService(activity: Activity, context: Context, resultCode: Int, data: Intent, pitch: Float?, speed: Float?) {
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
    private fun checkOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else return true
    }

    suspend fun isPermissionGiven(context: Context) {
        if (checkOverlayPermission(context)) {
            resultChannel.send(Result.PermissionTrue)
        } else {
            resultChannel.send(Result.PermissionFalse)
        }
    }

    suspend fun checkResult(resultCode: Int, resultOk: Int, activity: Activity, context: Context, data: Intent?, pitch: Float?, speed: Float?) {
        if (resultCode == resultOk) {
            if (data != null) {
                startService(activity, context, resultCode, data, pitch, speed)
            }
        } else {
            resultChannel.send(Result.EnableElements)
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>, activity: Activity): Boolean {
        val manager =
            activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name.equals(service.service.className)) {
                return true
            }
        }
        return false
    }

    suspend fun checkIfServiceIsRunningAndAppDisableElements(activity: Activity) {
        if (isMyServiceRunning(ScreenReaderService::class.java, activity)) {
            resultChannel.send(Result.DisableElementsIfServiceRunningAndApp)
        }
    }

    private fun checkForTTSEngines(context: Context): List<TextToSpeech.EngineInfo>? {
        return TextToSpeech(context) {}.engines
    }

    suspend fun showMessageTTSEngines(context: Context) {
        if (checkForTTSEngines(context)?.isEmpty() == true) {
            resultChannel.send(Result.TTSEngineNotAvailable("There is no TextToSpeech Engine available on your phone."))
        }
    }
}

sealed class Result {
    object EnableElements: Result()
    object PermissionTrue: Result()
    object PermissionFalse: Result()
    object DisableElementsIfServiceRunningAndApp: Result()
    class TTSEngineNotAvailable(val message: String) : Result()
}