package com.jamal.myread.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jamal.myread.utils.NavigationItem

class NavigationViewModel : ViewModel() {
    private var mutableNavigation = MutableLiveData<NavigationItem>()
    val navigation: LiveData<NavigationItem> = mutableNavigation

    fun navigateTo(navigationItem: NavigationItem) {
        mutableNavigation.postValue(navigationItem)
    }
}