package com.jamal.myread.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.jamal.myread.model.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    fun startService(activity: Activity, context: Context) {
        repository.startService(activity, context)
    }
}