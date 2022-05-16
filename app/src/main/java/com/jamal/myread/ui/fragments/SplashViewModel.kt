package com.jamal.myread.ui.fragments

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.jamal.myread.sharedpreferences.SharedPreferenceOnBoarding
import com.jamal.myread.sharedpreferences.SharedPreferencesKeys
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class SplashViewModel : ViewModel() {

    private val navigateChannel = Channel<NavigateTo>()
    val navigate = navigateChannel.receiveAsFlow()

    suspend fun navigateTo(activity: Activity) {

        if (SharedPreferenceOnBoarding.getPreferences(
                activity,
                SharedPreferencesKeys.ON_BOARDING_IS_FINISHED
            )
        ) {
            navigateChannel.send(NavigateTo.MainToHomeFragment)
        } else {
            navigateChannel.send(NavigateTo.MainToOnBoardingFragment)
        }
    }
}

sealed class NavigateTo {
    object MainToHomeFragment : NavigateTo()
    object MainToOnBoardingFragment : NavigateTo()
}

