package com.jamal.myread.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.jamal.myread.R
import com.jamal.myread.databinding.ActivityMainBinding
import com.jamal.myread.utils.DataStoreOnBoarding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var dataStoreOnBoarding: DataStoreOnBoarding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_MyRead)
        setContentView(binding.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.light_purple)
    }
}