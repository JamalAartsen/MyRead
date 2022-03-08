package com.jamal.myread.ui.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.jamal.myread.R
import com.jamal.myread.databinding.ActivityMainBinding
import com.jamal.myread.utils.DataStoreOnBoarding
import com.jamal.myread.utils.PreferencesKeys
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        setSupportActionBar(binding.toolbar)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.light_purple)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        lifecycleScope.launch {
            if (dataStoreOnBoarding.readOnBoardingPreference(applicationContext, PreferencesKeys.IS_ON_BOARDING_FINISHED)) {
                navController.navigate(R.id.homeFragment)
            } else {
                navController.navigate(R.id.viewPagerFragment)
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.viewPagerFragment) {
                binding.toolbar.visibility = View.GONE
            } else {
                binding.toolbar.visibility = View.VISIBLE
            }
        }
    }
}