package com.jamal.myread

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jamal.myread.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_MyRead)
        setContentView(binding.root)
    }
}