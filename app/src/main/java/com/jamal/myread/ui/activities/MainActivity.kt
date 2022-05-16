package com.jamal.myread.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.jamal.myread.R
import com.jamal.myread.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_MyRead)
        setContentView(binding.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.light_purple)

        val widthDp = resources.displayMetrics.run { widthPixels / density }
        val heightDp = resources.displayMetrics.run { heightPixels / density }
        Log.d("SizeScreen", "Height: $heightDp")
        Log.d("SizeScreen", "Width: $widthDp")

    }
}