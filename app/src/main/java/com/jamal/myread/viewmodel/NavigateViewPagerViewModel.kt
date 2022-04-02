package com.jamal.myread.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NavigateViewPagerViewModel: ViewModel() {
    private val mutableItem = MutableLiveData<Int>()
    val item = mutableItem

    fun navigateTo(item: Int) {
        mutableItem.postValue(item)
    }
}